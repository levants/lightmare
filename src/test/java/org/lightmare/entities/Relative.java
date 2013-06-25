package org.lightmare.entities;

import java.io.IOException;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.lightmare.rest.utils.RestUtils;

@Entity
@Table(name = "RELATIVES", schema = "PERSONS")
public class Relative implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "org.lightmare.entities.Relative")
    @TableGenerator(name = "org.lightmare.entities.Relative", table = "ID_GENERATORS", pkColumnName = "TABLE_NAME", pkColumnValue = "RELATIVES", valueColumnName = "KEY_VALUE", allocationSize = 20)
    @Column(name = "RELATIVE_ID")
    private Integer relativeId;

    private String name;

    public Integer getRelativeId() {
	return relativeId;
    }

    public void setRelativeId(Integer relativeId) {
	this.relativeId = relativeId;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public static Relative valueOf(String json) {

	Relative relative = null;
	try {
	    relative = RestUtils.convert(json, Relative.class);
	} catch (IOException ex) {
	    ex.printStackTrace();
	}

	return relative;
    }
}
