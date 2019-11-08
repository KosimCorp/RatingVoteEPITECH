/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kosim.app.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 *
 * @author user
 */
public interface Entity {
    public void cast(ResultSet rs) throws SQLException;
    public Map<String, Object> toMap() throws Exception;
    public void fromMap(Map<String, Object> map) throws Exception;
}
