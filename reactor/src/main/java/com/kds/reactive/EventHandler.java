package com.kds.reactive;

import java.nio.channels.SelectionKey;

public interface EventHandler {

    void handleEvent(SelectionKey key) throws Exception;
}
