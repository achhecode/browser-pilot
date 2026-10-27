
package com.achhecode.pinpointservice.service;

import com.achhecode.pinpointservice.PinpointanswerUrls;
import com.achhecode.pinpointservice.page.AnswerPage;
import com.achhecode.pinpointservice.page.PPMiniSudokuPage;
import com.achhecode.pinpointservice.page.PPPatchesPage;
import com.achhecode.pinpointservice.page.PPPinpointAnswerPage;
import com.achhecode.pinpointservice.page.PPQueensPage;
import com.achhecode.pinpointservice.page.PPTangoPage;
import com.achhecode.pinpointservice.page.PPWendPage;
import com.achhecode.pinpointservice.page.PPZipPage;
import com.achhecode.browserpilotdto.dto.MiniSudokuResponse;
import com.achhecode.browserpilotdto.dto.PatchesResponse;
import com.achhecode.browserpilotdto.dto.PinpointResponse;
import com.achhecode.browserpilotdto.dto.QueensResponse;
import com.achhecode.browserpilotdto.dto.TangoResponse;
import com.achhecode.browserpilotdto.dto.WendResponse;
import com.achhecode.browserpilotdto.dto.ZipResponse;
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

    public ZipResponse retrieveZipAnswer() {
        return retrieveAnswer(PinpointanswerUrls.ZIP, PPZipPage::new);
    }

    public QueensResponse retrieveQueenAnswer() {
        return retrieveAnswer(PinpointanswerUrls.QUEENS, PPQueensPage::new);
    }

    public TangoResponse retrieveTangoAnswer() {
        return retrieveAnswer(PinpointanswerUrls.TANGO, PPTangoPage::new);
    }

    public WendResponse retrieveWendAnswer() {
        return retrieveAnswer(PinpointanswerUrls.WEND, PPWendPage::new);
    }

    // PPPatchesResults

    public PatchesResponse retrievePatchesAnswer() {
        return retrieveAnswer(PinpointanswerUrls.PATCHES, PPPatchesPage::new);
    }

    public MiniSudokuResponse retrieveMiniSudokuAnswer() {
        return retrieveAnswer(PinpointanswerUrls.MINI_SUDOKU, PPMiniSudokuPage::new);
    }

    public PinpointResponse retrievePinpointAnswer() {
        return retrieveAnswer(PinpointanswerUrls.PINPOINT, PPPinpointAnswerPage::new);
    }

    // retrievePinpointAnswer

    private <T, P extends AnswerPage<T>> T retrieveAnswer(String url, Function<Page, P> pageFactory) {
        Page page = pageResolver.findPage();
        page.navigate(url);

        P pageObject = pageFactory.apply(page);
        return pageObject.getAnswer();
    }
}
