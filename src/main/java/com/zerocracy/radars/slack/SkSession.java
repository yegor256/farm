/*
 * Copyright (c) 2016-2018 Zerocracy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to read
 * the Software only. Permissions is hereby NOT GRANTED to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zerocracy.radars.slack;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackUser;
import java.io.Closeable;

/**
 * Session in Slack.
 *
 * @since 1.0
 * @todo #89:30min Use this interface and its implementation RealSkSession
 *  everywhere instead of SlackSession that is used currently. When it's done
 *  some tests can be refactored to get rid of Mockito library.
 */
public interface SkSession extends Closeable {
    /**
     * Find channel by its identifier.
     * @param id Channel identifier
     * @return Slack channel
     */
    SlackChannel channel(String id);

    /**
     * Find user by its identifier.
     * @param id Channel identifier
     * @return Slack user
     */
    SlackUser user(String id);

    /**
     * Send a message to specific channel.
     * @param channel Slack channel
     * @param message Message
     */
    void send(SlackChannel channel, String message);

    /**
     * Send a message to specific user.
     * @param user Slack user
     * @param message Message
     */
    void send(SlackUser user, String message);

    /**
     * Check whether the current session belongs to a channel with specific
     * identifier.
     * @param id Channel identifier
     * @return Where this session belongs to the given channel
     */
    boolean hasChannel(String id);
}
