package com.achhecode.browser_pilot.keyboardmonitor.service;

import com.achhecode.browser_pilot.keyboardmonitor.domain.KeyboardEvent;
import com.achhecode.browser_pilot.keyboardmonitor.domain.KeyboardMonitorMode;
import com.achhecode.browser_pilot.keyboardmonitor.domain.KeyboardSession;
import com.achhecode.browser_pilot.keyboardmonitor.filter.AllKeyboardEventFilter;
import com.achhecode.browser_pilot.keyboardmonitor.filter.DirectionKeyboardEventFilter;
import com.achhecode.browser_pilot.keyboardmonitor.filter.KeyboardEventFilter;
import com.achhecode.browser_pilot.keyboardmonitor.filter.NumberKeyboardEventFilter;
import com.achhecode.browser_pilot.keyboardmonitor.source.KeyboardEventSource;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DefaultKeyboardMonitorService
        implements KeyboardMonitorService {

    private final Map<KeyboardMonitorMode, KeyboardEventFilter>
            filters;

    private final Map<UUID, KeyboardSession>
            sessions = new ConcurrentHashMap<>();

    private final KeyboardEventSource eventSource;

    private UUID activeSessionId;

    public DefaultKeyboardMonitorService(
            DirectionKeyboardEventFilter directionFilter,
            NumberKeyboardEventFilter numberFilter,
            AllKeyboardEventFilter allFilter,
            KeyboardEventSource eventSource
    ) {

        this.eventSource = eventSource;

        this.filters =
                new EnumMap<>(KeyboardMonitorMode.class);

        filters.put(
                KeyboardMonitorMode.DIRECTION,
                directionFilter
        );

        filters.put(
                KeyboardMonitorMode.NUMBER,
                numberFilter
        );

        filters.put(
                KeyboardMonitorMode.ALL,
                allFilter
        );
    }

    @Override
    public synchronized KeyboardSession start(
            KeyboardMonitorMode mode
    ) {

        if (activeSessionId != null) {

            KeyboardSession activeSession =
                    sessions.get(activeSessionId);

            if (activeSession != null
                    && activeSession.isRunning()) {

                throw new IllegalStateException(
                        "A keyboard monitoring session is already running: "
                                + activeSessionId
                );
            }
        }

        KeyboardSession session =
                new KeyboardSession(
                        UUID.randomUUID(),
                        mode
                );

        sessions.put(
                session.getId(),
                session
        );

        activeSessionId = session.getId();

        try {

            eventSource.start(
                    event ->
                            accept(
                                    session.getId(),
                                    event
                            )
            );

            return session;

        } catch (RuntimeException e) {

            sessions.remove(session.getId());
            activeSessionId = null;

            throw e;
        }
    }

    @Override
    public synchronized KeyboardSession stop(
            UUID sessionId
    ) {

        KeyboardSession session =
                getSession(sessionId);

        if (session.isRunning()) {
            eventSource.stop();
            session.stop();
        }

        if (sessionId.equals(activeSessionId)) {
            activeSessionId = null;
        }

        return session;
    }

    @Override
    public KeyboardSession status(
            UUID sessionId
    ) {

        return getSession(sessionId);
    }

    @Override
    public void accept(
            UUID sessionId,
            KeyboardEvent event
    ) {

        KeyboardSession session =
                getSession(sessionId);

        if (!session.isRunning()) {
            return;
        }

        KeyboardEventFilter filter =
                filters.get(session.getMode());

        if (filter == null) {

            throw new IllegalStateException(
                    "No keyboard event filter configured for mode: "
                            + session.getMode()
            );
        }

        if (filter.supports(event)) {
            session.record(event);
        }
    }

    private KeyboardSession getSession(
            UUID sessionId
    ) {

        KeyboardSession session =
                sessions.get(sessionId);

        if (session == null) {

            throw new IllegalArgumentException(
                    "Keyboard session not found: "
                            + sessionId
            );
        }

        return session;
    }
}