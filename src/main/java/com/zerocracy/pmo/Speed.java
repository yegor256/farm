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
package com.zerocracy.pmo;

import com.jcabi.aspects.Tv;
import com.zerocracy.Farm;
import com.zerocracy.Item;
import com.zerocracy.Policy;
import com.zerocracy.Xocument;
import java.io.IOException;
import org.cactoos.collection.Mapped;
import org.cactoos.iterable.ItemAt;
import org.cactoos.scalar.IoCheckedScalar;
import org.xembly.Directives;

/**
 * Speed of delivery.
 * <p>
 * User metric which represents how much time user spend to finish a job.
 *
 * @since 1.0
 * @todo #860:30min Modify the stakeholder where the payments are made and
 *  use method Speed.bonus(...) to pay the user a bonus in minutes if they
 *  completed the job in less than 48 hours.
 */
public final class Speed {

    /**
     * PMO.
     */
    private final Pmo pmo;

    /**
     * Login of the person.
     */
    private final String login;

    /**
     * Ctor.
     * @param farm The farm
     * @param user The user
     */
    public Speed(final Farm farm, final String user) {
        this(new Pmo(farm), user);
    }

    /**
     * Ctor.
     * @param pkt PMO
     * @param user The user
     */
    public Speed(final Pmo pkt, final String user) {
        this.pmo = pkt;
        this.login = user;
    }

    /**
     * Bootstrap it.
     * @return This
     * @throws IOException If fails
     */
    public Speed bootstrap() throws IOException {
        try (final Item item = this.item()) {
            new Xocument(item.path()).bootstrap("pmo/speed");
        }
        return this;
    }

    /**
     * Add points to the list.
     * @param project A project
     * @param job A job
     * @param minutes How many minutes was spent on this job
     * @throws IOException If fails
     */
    public void add(final String project, final String job, final long minutes)
        throws IOException {
        try (final Item item = this.item()) {
            new Xocument(item.path()).modify(
                new Directives()
                    .xpath("/speed")
                    .add("order")
                    .attr("job", job)
                    .add("project")
                    .set(project)
                    .up()
                    .add("minutes")
                    .set(minutes)
                    .up()
                    .up()
            );
        }
    }

    /**
     * Average speed in minutes for user.
     * @return Minutes
     * @throws IOException If fails
     */
    public double avg() throws IOException {
        try (final Item item = this.item()) {
            return new IoCheckedScalar<>(
                new ItemAt<>(
                    0.0,
                    new Mapped<>(
                        Double::parseDouble,
                        new Xocument(item.path())
                            .xpath("avg(/speed/order/minutes)")
                    )
                )
            ).value();
        }
    }

    /**
     * If a job is completed in less than 48h, the user gets a
     * bonus in minutes.
     * @param job Job's id.
     * @return The bonus for the speed of the job.
     * @throws IOException If something goes wrong.
     */
    public int bonus(final String job) throws IOException {
        final int bonus;
        try (final Item item = this.item()) {
            final int minutes = Integer.parseInt(
                new Xocument(item.path())
                    .xpath(
                        String.format(
                            "/speed/order[@job='%s']/minutes/text()", job
                        )
                    ).get(0)
            );
            final int limit = new Policy().get("36.hours", 48) * Tv.SIXTY;
            if (minutes <= limit) {
                bonus = Tv.FIVE;
            } else {
                bonus = 0;
            }
        }
        return bonus;
    }

    /**
     * Empty document.
     * @return True if person does not have any speed record
     * @throws IOException If fails
     */
    public boolean isEmpty() throws IOException {
        try (final Item item = this.item()) {
            return new Xocument(item.path()).nodes("/speed/order").isEmpty();
        }
    }

    /**
     * Return full list of jobs.
     * @return List of job IDs
     * @throws IOException If fails
     */
    public Iterable<String> jobs() throws IOException {
        try (final Item item = this.item()) {
            return new Xocument(item.path()).xpath(
                "/speed/order/@job"
            );
        }
    }

    /**
     * The item.
     * @return Item
     * @throws IOException If fails
     */
    private Item item() throws IOException {
        return this.pmo.acq(
            String.format("speed/%s.xml", this.login)
        );
    }
}
