package com.migratorydata;

/**
 * The quality of service (QoS) levels for MigratoryData messaging.
 */
public enum QoS {
    /**
     * The <code>QoS.STANDARD</code> should be used for noncritical messages which will not be included in the cache
     * of the MigratoryData cluster. In this way, after a connection recovery, a client will not receive as part of
     * the recovery process the messages with <code>QoS.STANDARD</code>.
     *
     * &lt;p&gt;
     *     Note: This QoS level corresponds to the <code>at-most-once</code> delivery semantics.
     * &lt;/p&gt;
     */
    STANDARD(0),

    /**
     * The <code>QoS.GUARANTEED</code> should be used for critical messages which will be included in the cache of
     * the MigratoryData cluster. In this way, after a connection recovery, a client will receive as part of the
     * recovery process the messages with <code>QoS.GUARANTEED</code>.
     *
     * &lt;p&gt;
     *     Note: This QoS level corresponds to the <code>at-least-once</code> delivery semantics. Also, it might
     *     correspond to the <code>exactly-once</code> delivery semantics provided that subscribers are responsible
     *     for filtering out duplicate receptions, if it at all matters for the application. Typically, a small
     *     buffer containing the identifiers of recently-received messages is sufficient for this task.
     * &lt;/p&gt;
     */
    GUARANTEED(1);

    private final int code;

    QoS(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}