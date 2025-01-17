/*
 * Copyright (C) 2024 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
 * Karlsruhe, Germany.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fraunhofer.iosb.ilt.frostserver.persistence.pgjooq.utils.fieldmapper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableField;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorString;
import de.fraunhofer.iosb.ilt.frostserver.persistence.pgjooq.JooqPersistenceManager;
import de.fraunhofer.iosb.ilt.frostserver.persistence.pgjooq.tables.StaMainTable;
import de.fraunhofer.iosb.ilt.frostserver.persistence.pgjooq.utils.PropertyFieldRegistry;
import de.fraunhofer.iosb.ilt.frostserver.persistence.pgjooq.utils.PropertyFieldRegistry.ConverterPassword;
import de.fraunhofer.iosb.ilt.frostserver.persistence.pgjooq.utils.PropertyFieldRegistry.ExpressionFactory;
import de.fraunhofer.iosb.ilt.frostserver.property.EntityProperty;
import java.util.HashMap;
import java.util.Map;
import org.jooq.Name;
import org.jooq.Table;

/**
 *
 * @author hylke
 */
public class FieldMapperPassword extends FieldMapperAbstractEp {

    public static final String TAG_PLAIN_TEXT_PASSWORD = "plainTextPassword";

    @ConfigurableField(editor = EditorString.class,
            label = "Field", description = "The database field to use.")
    @EditorString.EdOptsString()
    private String field;

    /**
     * The index of the String field in the table.
     */
    @JsonIgnore
    private int fieldIdx;

    @Override
    public void registerField(JooqPersistenceManager ppm, StaMainTable staTable) {
        final Name tableName = staTable.getQualifiedName();
        Table<?> dbTable = ppm.getDbTable(tableName);
        fieldIdx = getOrRegisterField(field, dbTable, staTable);
    }

    @Override
    public <T extends StaMainTable<T>> void registerMapping(JooqPersistenceManager ppm, T table) {
        final boolean plainTextPw = ppm.getCoreSettings().getAuthSettings().getBoolean(TAG_PLAIN_TEXT_PASSWORD, true);
        final EntityProperty entityProperty = getParent().getEntityProperty();
        final PropertyFieldRegistry<T> pfReg = table.getPropertyFieldRegistry();
        final int idx = fieldIdx;
        final ExpressionFactory<T> factory = t -> t.field(idx);
        pfReg.addEntry(entityProperty, factory, new ConverterPassword<>(plainTextPw, entityProperty, factory));
    }

    /**
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * @param field the field to set
     * @return this.
     */
    public FieldMapperPassword setField(String field) {
        this.field = field;
        return this;
    }

    @Override
    public Map<String, String> getFieldTypes() {
        Map<String, String> value = new HashMap<>();
        value.put(field, "VARCHAR(255)");
        return value;
    }

}
