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

package com.torodb.torod.db.backends.converters;

import com.google.common.collect.Table;
import com.torodb.torod.core.subdocument.SplitDocument;
import com.torodb.torod.core.subdocument.SubDocType;
import com.torodb.torod.core.subdocument.structure.DocStructure;
import com.torodb.torod.core.subdocument.structure.StructureElementDFW;
import com.torodb.torod.db.backends.meta.IndexStorage;

import java.io.Serializable;

/**
 *
 */
public class SplitDocumentConverter implements Serializable {

    private static final SubDocConverter SUB_DOC_CONVERTER = new SubDocConverter();

    public static final SplitDocumentConverter INSTANCE = new SplitDocumentConverter();
    private static final long serialVersionUID = 1L;

    private SplitDocumentConverter() {
    }

    public SplitDocument convert(
            IndexStorage.CollectionSchema colSchema,
            int docId, 
            int structureId, 
            Table<Integer, Integer, String> databaseInfo
    ) {
        SplitDocument.Builder builder = new SplitDocument.Builder();

        DocStructure structure = getStructure(colSchema, structureId);

        builder.setRoot(structure);

        SubDocAdder adder = new SubDocAdder(databaseInfo, builder, colSchema);
        
        structure.accept(adder, null);
        
        builder.setId(docId);

        return builder.build();
    }

    private DocStructure getStructure(
            IndexStorage.CollectionSchema colSchema,
            int structureId
    ) {
        DocStructure structure 
                = colSchema.getStructuresCache().getStructure(structureId);
        if (structure == null) {
            throw new IllegalStateException(
                    "There is no structure with id "+ structureId
            );
        }
        return structure;
    }

    private static class SubDocAdder extends StructureElementDFW<Void> {

        private final Table<Integer, Integer, String> databaseInfo;
        private final SplitDocument.Builder splitDocBuilder;
        private final IndexStorage.CollectionSchema colSchema;

        public SubDocAdder(
                Table<Integer, Integer, String> databaseInfo, 
                SplitDocument.Builder splitDocBuilder, 
                IndexStorage.CollectionSchema colSchema
        ) {
            this.databaseInfo = databaseInfo;
            this.splitDocBuilder = splitDocBuilder;
            this.colSchema = colSchema;
        }

        @Override
        protected void preDocStructure(DocStructure docStructure, Void arg) {
            SubDocType type = docStructure.getType();
            int typeId = colSchema.getTypeId(type);
            int index = docStructure.getIndex();

            String subDocAsJson = databaseInfo.get(typeId, index);
            
            SubDocType subDocType = colSchema.getSubDocType(typeId);

            splitDocBuilder.add(SUB_DOC_CONVERTER.from(subDocAsJson, subDocType));
        }

    }
}
