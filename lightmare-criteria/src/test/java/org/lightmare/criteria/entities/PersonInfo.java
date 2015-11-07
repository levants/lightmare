package org.lightmare.criteria.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PersonInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "CARD_NUMBER")
    private String cardNumber;

    @Column(name = "NOTE")
    private String note;

    public String getCardNumber() {
	return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
	this.cardNumber = cardNumber;
    }

    public String getNote() {
	return note;
    }

    public void setNote(String note) {
	this.note = note;
    }
}
