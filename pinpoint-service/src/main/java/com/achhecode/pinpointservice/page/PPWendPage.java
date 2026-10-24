
package com.achhecode.pinpointservice.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.achhecode.browserpilotdto.dto.Coordinates;
import com.achhecode.browserpilotdto.dto.Direction;
import com.achhecode.browserpilotdto.dto.WendResponse;
import com.achhecode.browserpilotdto.dto.WendResult;
import com.achhecode.pinpointservice.PinpointanswerUrls;
import com.achhecode.pinpointservice.handler.WebsitePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class PPWendPage extends WebsitePage implements AnswerPage<WendResponse> {

    private static final String REVEAL_ALL_XPATH = "//button[normalize-space(.)='Reveal all']";

    private static final String TOTAL_CELL_XPATH = "//button[@data-wend-answer-cell]";

    private static final String BLOCKED_CELL_XPATH = "//span[@aria-label='Blocked cell']";

    private static final String COMPLETE_WORD_XPATH = "//p[contains(@class, 'WendAnswerViewer_completeWord')]";

    private static final Pattern LABEL_PATTERN = Pattern
            .compile("^(.+?) at row (\\d+), column (\\d+)\\s*[—-]\\s*revealed$");

    private static final Pattern COLOR_PATTERN = Pattern.compile("--word-color\\s*:\\s*([^;]+)");

    private static final Pattern INK_PATTERN = Pattern.compile("--word-ink\\s*:\\s*([^;]+)");

    public PPWendPage(Page page) {
        super(page);
    }

    @Override
    public boolean isDisplayed() {
        return page.url().equals(PinpointanswerUrls.WEND);
    }

    @Override
    public WendResponse getAnswer() {
        return new WendResponse(getWords());
    }

    public void clickRevealAll() {
        page.locator(REVEAL_ALL_XPATH).click();
    }

    public int totalCell() {
        return page.locator(TOTAL_CELL_XPATH).count() + page.locator(BLOCKED_CELL_XPATH).count();
    }

    public int calculateGridSize() {
        int total = totalCell();
        int gridSize = (int) Math.sqrt(total);

        if (gridSize * gridSize != total) {
            throw new IllegalStateException("Grid cells do not form a square. Total cells: " + total);
        }

        return gridSize;
    }

    public List<WendResult> getWords() {
        clickRevealAll();

        // Wait for the reveal animation and word list to finish loading.
        page.waitForTimeout(30_000);

        calculateGridSize();

        List<String> completeWords = readCompleteWords();

        Map<WordStyle, List<Cell>> groups = readCellsGroupedByStyle();

        List<WendResult> results = new ArrayList<>();
        Set<WordStyle> usedStyles = new HashSet<>();

        for (String word : completeWords) {
            WordStyle matchedStyle = findMatchingStyle(word, groups, usedStyles);

            if (matchedStyle == null) {
                throw new IllegalStateException("Could not match revealed word to its cells: " + word);
            }

            List<Cell> cells = groups.get(matchedStyle);

            List<Cell> orderedCells = findOrderedPath(word, cells);

            if (orderedCells == null) {
                throw new IllegalStateException("Could not find a coordinate path spelling: " + word);
            }

            results.add(buildResult(word, orderedCells));
            usedStyles.add(matchedStyle);
        }

        return results;
    }

    private List<String> readCompleteWords() {
        Locator wordElements = page.locator(COMPLETE_WORD_XPATH);
        List<String> words = new ArrayList<>();

        for (int i = 0; i < wordElements.count(); i++) {
            String word = wordElements.nth(i).innerText().trim();

            if (!word.isEmpty()) {
                words.add(word);
            }
        }

        if (words.isEmpty()) {
            throw new IllegalStateException("No completed words found after clicking Reveal all");
        }

        return words;
    }

    private Map<WordStyle, List<Cell>> readCellsGroupedByStyle() {
        Map<WordStyle, List<Cell>> groups = new LinkedHashMap<>();

        Locator buttons = page.locator(TOTAL_CELL_XPATH);

        for (int i = 0; i < buttons.count(); i++) {
            Locator button = buttons.nth(i);

            String ariaLabel = button.getAttribute("aria-label");
            String style = button.getAttribute("style");

            if (ariaLabel == null || style == null) {
                continue;
            }

            Matcher matcher = LABEL_PATTERN.matcher(ariaLabel.trim());

            if (!matcher.matches()) {
                continue;
            }

            String letter = matcher.group(1).trim();
            int row = Integer.parseInt(matcher.group(2));
            int col = Integer.parseInt(matcher.group(3));

            String color = extractStyleValue(COLOR_PATTERN, style);
            String ink = extractStyleValue(INK_PATTERN, style);

            if (color == null || ink == null) {
                continue;
            }

            WordStyle key = new WordStyle(color, ink);

            groups.computeIfAbsent(key, ignored -> new ArrayList<>()).add(new Cell(letter, row, col));
        }

        return groups;
    }

    private String extractStyleValue(Pattern pattern, String style) {
        Matcher matcher = pattern.matcher(style);

        return matcher.find() ? matcher.group(1).trim().toLowerCase() : null;
    }

    private WordStyle findMatchingStyle(String word, Map<WordStyle, List<Cell>> groups, Set<WordStyle> usedStyles) {
        Map<Character, Integer> expectedLetters = letterCounts(word);

        for (Map.Entry<WordStyle, List<Cell>> entry : groups.entrySet()) {
            WordStyle style = entry.getKey();
            List<Cell> cells = entry.getValue();

            if (usedStyles.contains(style)) {
                continue;
            }

            if (cells.size() == word.length() && letterCounts(cells).equals(expectedLetters)) {
                return style;
            }
        }

        return null;
    }

    @SuppressWarnings("null")
    private Map<Character, Integer> letterCounts(String word) {
        Map<Character, Integer> counts = new HashMap<>();

        for (char letter : word.toUpperCase().toCharArray()) {
            counts.merge(letter, 1, Integer::sum);
        }

        return counts;
    }

    @SuppressWarnings("null")
    private Map<Character, Integer> letterCounts(List<Cell> cells) {
        Map<Character, Integer> counts = new HashMap<>();

        for (Cell cell : cells) {
            String letter = cell.letter().toUpperCase();

            if (letter.length() != 1) {
                throw new IllegalStateException("Expected a single letter per cell, got: " + letter);
            }

            counts.merge(letter.charAt(0), 1, Integer::sum);
        }

        return counts;
    }

    private List<Cell> findOrderedPath(String word, List<Cell> cells) {
        if (cells.size() != word.length()) {
            return null;
        }

        Map<Position, Cell> cellMap = new HashMap<>();

        for (Cell cell : cells) {
            cellMap.put(new Position(cell.row(), cell.col()), cell);
        }

        for (Cell start : cells) {
            if (!matchesLetter(start, word.charAt(0))) {
                continue;
            }

            List<Cell> path = new ArrayList<>();
            Set<Position> visited = new HashSet<>();

            if (searchPath(word.toUpperCase(), 0, start, cellMap, visited, path)) {
                return path;
            }
        }

        return null;
    }

    private boolean searchPath(String word, int index, Cell current, Map<Position, Cell> cellMap, Set<Position> visited,
            List<Cell> path) {
        Position position = new Position(current.row(), current.col());

        if (!visited.add(position)) {
            return false;
        }

        path.add(current);

        if (index == word.length() - 1) {
            return true;
        }

        for (Cell neighbor : getNeighbors(current, cellMap)) {
            if (matchesLetter(neighbor, word.charAt(index + 1))
                    && !visited.contains(new Position(neighbor.row(), neighbor.col()))) {

                if (searchPath(word, index + 1, neighbor, cellMap, visited, path)) {
                    return true;
                }
            }
        }

        path.remove(path.size() - 1);
        visited.remove(position);

        return false;
    }

    private boolean matchesLetter(Cell cell, char expected) {
        return cell.letter().equalsIgnoreCase(String.valueOf(expected));
    }

    private List<Cell> getNeighbors(Cell cell, Map<Position, Cell> cellMap) {
        int row = cell.row();
        int col = cell.col();

        List<Cell> neighbors = new ArrayList<>();

        addIfPresent(neighbors, cellMap, row - 1, col);
        addIfPresent(neighbors, cellMap, row + 1, col);
        addIfPresent(neighbors, cellMap, row, col - 1);
        addIfPresent(neighbors, cellMap, row, col + 1);

        return neighbors;
    }

    private void addIfPresent(List<Cell> neighbors, Map<Position, Cell> cellMap, int row, int col) {
        Cell neighbor = cellMap.get(new Position(row, col));

        if (neighbor != null) {
            neighbors.add(neighbor);
        }
    }

    private WendResult buildResult(String word, List<Cell> orderedCells) {
        List<Coordinates> coordinates = new ArrayList<>();
        List<Direction> directions = new ArrayList<>();

        for (Cell cell : orderedCells) {
            coordinates.add(new Coordinates(cell.row(), cell.col()));
        }

        for (int i = 1; i < orderedCells.size(); i++) {
            directions.add(getDirection(orderedCells.get(i - 1), orderedCells.get(i)));
        }

        return new WendResult(word, coordinates, coordinates.get(0), directions);
    }

    private Direction getDirection(Cell from, Cell to) {
        if (to.row() == from.row() - 1 && to.col() == from.col()) {
            return Direction.UP;
        }

        if (to.row() == from.row() + 1 && to.col() == from.col()) {
            return Direction.DOWN;
        }

        if (to.row() == from.row() && to.col() == from.col() - 1) {
            return Direction.LEFT;
        }

        if (to.row() == from.row() && to.col() == from.col() + 1) {
            return Direction.RIGHT;
        }

        throw new IllegalStateException("Non-adjacent cells: " + from + " -> " + to);
    }

    private record WordStyle(String color, String ink) {
    }

    private record Position(int row, int col) {
    }

    private record Cell(String letter, int row, int col) {
    }
}
