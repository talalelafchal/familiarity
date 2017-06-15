package com.module.candychat.net.event;

import com.module.candychat.net.model.RelationsGroup;

/**
 * Created by Mac on 8/3/15.
 */
public class GetTopicSuccess {
    public RelationsGroup info;

    public GetTopicSuccess(RelationsGroup info) {
        this.info = info;
    }
}
