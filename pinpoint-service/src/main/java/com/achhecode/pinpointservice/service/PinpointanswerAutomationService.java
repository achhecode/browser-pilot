
package com.achhecode.pinpointservice.service;

import com.achhecode.pinpointservice.PinpointanswerUrls;
import com.achhecode.pinpointservice.page.AnswerPage;
import com.achhecode.pinpointservice.page.PPMiniSudokuPage;
import com.achhecode.pinpointservice.page.PPPatchesPage;
import com.achhecode.pinpointservice.page.PPQueensPage;
import com.achhecode.pinpointservice.page.PPTangoPage;
import com.achhecode.pinpointservice.page.PPWendPage;
import com.achhecode.pinpointservice.page.PPZipPage;
import com.achhecode.browserpilotdto.dto.PPMiniSudokuResult;
import com.achhecode.browserpilotdto.dto.PPPatchesResults;
import com.achhecode.browserpilotdto.dto.PPQueensResult;
import com.achhecode.browserpilotdto.dto.PPTangoResults;
import com.achhecode.browserpilotdto.dto.PPWendResults;
import com.achhecode.browserpilotdto.dto.PPZipResult;
import com.achhecode.pinpointservice.PinpointanswerPageResolver;
import com.microsoft.playwright.Page;
import org.springframework.stereotype.Service;
import java.util.function.Function;

@Service
public class PinpointanswerAutomationService {

    private final PinpointanswerPageResolver pageResolver;

    public PinpointanswerAutomationService(PinpointanswerPageResolver pageResolver) {
        this.pageResolver = pageResolver;
    }

    public PPZipResult retrieveZipAnswer() {
        return retrieveAnswer(PinpointanswerUrls.ZIP, PPZipPage::new);
    }

    public PPQueensResult retrieveQueenAnswer() {
        return retrieveAnswer(PinpointanswerUrls.QUEENS, PPQueensPage::new);
    }

    public PPTangoResults retrieveTangoAnswer() {
        return retrieveAnswer(PinpointanswerUrls.TANGO, PPTangoPage::new);
    }

    public PPWendResults retrieveWendAnswer() {
        return retrieveAnswer(PinpointanswerUrls.WEND, PPWendPage::new);
    }

    // PPPatchesResults

    public PPPatchesResults retrievePatchesAnswer() {
        return retrieveAnswer(PinpointanswerUrls.PATCHES, PPPatchesPage::new);
    }

    public PPMiniSudokuResult retrieveMiniSudokuAnswer() {
        return retrieveAnswer(PinpointanswerUrls.MINI_SUDOKU, PPMiniSudokuPage::new);
    }

    private <T, P extends AnswerPage<T>> T retrieveAnswer(String url, Function<Page, P> pageFactory) {
        Page page = pageResolver.findPage();
        page.navigate(url);

        P pageObject = pageFactory.apply(page);
        return pageObject.getAnswer();
    }
}
