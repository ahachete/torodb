/*
 *     This file is part of ToroDB.
 *
 *     ToroDB is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     ToroDB is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with ToroDB. If not, see <http://www.gnu.org/licenses/>.
 *
 *     Copyright (c) 2014, 8Kdata Technology
 *     
 */

package com.torodb.torod.db.backends.converters.jooq;

import com.torodb.torod.core.subdocument.BasicType;
import com.torodb.torod.core.subdocument.values.DateValue;
import java.sql.Date;
import org.jooq.DataType;
import org.jooq.impl.SQLDataType;
import org.threeten.bp.DateTimeUtils;

/**
 *
 */
public class DateValueConverter implements SubdocValueConverter<Date, DateValue> {
    private static final long serialVersionUID = 1L;

    @Override
    public DataType<Date> getDataType() {
        return SQLDataType.DATE;
    }

    @Override
    public BasicType getErasuredType() {
        return BasicType.DATE;
    }

    @Override
    public DateValue from(Date databaseObject) {
        return new DateValue(
                DateTimeUtils.toLocalDate(databaseObject)
        );
    }

    @Override
    public Date to(DateValue userObject) {
        return DateTimeUtils.toSqlDate(userObject.getValue());
    }

    @Override
    public Class<Date> fromType() {
        return Date.class;
    }

    @Override
    public Class<DateValue> toType() {
        return DateValue.class;
    }

}
