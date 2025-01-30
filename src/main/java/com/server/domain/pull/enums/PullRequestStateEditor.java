package com.server.domain.pull.enums;

import java.beans.PropertyEditorSupport;

// NOTE: Controller에서 소문자로 입력되어도 enum type으로 인식하기 위하여 필요
public class PullRequestStateEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(PullRequestState.fromValue(text));
    }
}
