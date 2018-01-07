package de.igloffstein.maik.arevelation.enums;

/**
 * Enum for available fields in revelation and aRevelation
 *
 * Created by OldSparkyMI on 22.11.17.
 */

public enum FieldType {
    // CreditCard
    CREDITCARD_CARDTYPE("creditcard-cardtype"),
    CREDITCARD_CARDNUMBER("creditcard-cardnumber"),
    CREDITCARD_EXPIRYDATE("creditcard-expirydate"),
    CREDITCARD_CCV("creditcard-ccv"),

    // generic
    GENERIC_HOSTNAME("generic-hostname"),
    GENERIC_CERTIFICATE("generic-certificate"),
    GENERIC_KEYFILE("generic-keyfile"),
    GENERIC_LOCATION("generic-location"),
    GENERIC_CODE("generic-code"),
    GENERIC_USERNAME("generic-username"),
    GENERIC_PASSWORD("generic-password"),
    GENERIC_DATABASE("generic-database"),
    GENERIC_EMAIL("generic-email"),
    GENERIC_PORT("generic-port"),
    GENERIC_DOMAIN("generic-domain"),
    PHONE_PHONENUMBER("phone-phonenumber"),
    GENERIC_PIN("generic-pin"),
    GENERIC_URL("generic-url");

    private final String id;
    public final FieldType canonical;

    FieldType(String id) {
        this(id, null);
    }

    FieldType(String id, FieldType canonical) {
        this.id = id;
        this.canonical = canonical;
    }

    @Override
    public String toString() {
        return this.getId();
    }

    public String getId() {
        return (null == this.canonical) ? id : this.canonical.id;
    }
}
