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
package com.zerocracy.pm;

import com.jcabi.log.Logger;
import com.zerocracy.Farm;
import com.zerocracy.Project;
import com.zerocracy.pmo.Pmo;

/**
 * Claim out which doesn't throw exceptions on post.
 *
 * @since 1.0
 */
public final class ClaimOutSafe {

    /**
     * Origin claim.
     */
    private final ClaimOut claim;

    /**
     * Ctor.
     *
     * @param origin Origin claim
     */
    public ClaimOutSafe(final ClaimOut origin) {
        this.claim = origin;
    }

    /**
     * Post to project without exception.
     *
     * @param project Project
     * @checkstyle IllegalCatchCheck (14 lines)
     */
    @SuppressWarnings({"overloads", "PMD.AvoidCatchingThrowable"})
    public void postTo(final Project project) {
        try {
            this.claim.postTo(project);
        } catch (final Throwable err) {
            Logger.error(
                this,
                "Failed to post claim: %[exception]s",
                err
            );
        }
    }

    /**
     * Post to PMO without exception.
     *
     * @param farm Farm
     */
    @SuppressWarnings("overloads")
    public void postTo(final Farm farm) {
        this.postTo(new Pmo(farm));
    }
}
