
package com.achhecode.browser_pilot.website.pinpointanswer.service;

import com.achhecode.browser_pilot.website.pinpointanswer.PinpointanswerPageResolver;
import com.achhecode.browser_pilot.website.pinpointanswer.PinpointanswerUrls;
import com.achhecode.browser_pilot.website.pinpointanswer.dto.PPPatchesResults;
import com.achhecode.browser_pilot.website.pinpointanswer.dto.PPQueensResult;
import com.achhecode.browser_pilot.website.pinpointanswer.dto.PPTangoResults;
import com.achhecode.browser_pilot.website.pinpointanswer.dto.PPWendResults;
import com.achhecode.browser_pilot.website.pinpointanswer.dto.PPZipResult;
import com.achhecode.browser_pilot.website.pinpointanswer.page.AnswerPage;
import com.achhecode.browser_pilot.website.pinpointanswer.page.PPPatchesPage;
import com.achhecode.browser_pilot.website.pinpointanswer.page.PPQueensPage;
import com.achhecode.browser_pilot.website.pinpointanswer.page.PPTangoPage;
import com.achhecode.browser_pilot.website.pinpointanswer.page.PPWendPage;
import com.achhecode.browser_pilot.website.pinpointanswer.page.PPZipPage;
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

    private <T, P extends AnswerPage<T>> T retrieveAnswer(String url, Function<Page, P> pageFactory) {
        Page page = pageResolver.findPage();
        page.navigate(url);

        P pageObject = pageFactory.apply(page);
        return pageObject.getAnswer();
    }
}
