/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package demoapp.dom.progmodel.customvaluetypes.embeddedvalues.jpa;

import javax.inject.Named;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import org.apache.causeway.applib.util.schema.CommonDtoUtils;
import org.apache.causeway.applib.value.semantics.Parser;
import org.apache.causeway.applib.value.semantics.Renderer;
import org.apache.causeway.applib.value.semantics.ValueDecomposition;
import org.apache.causeway.applib.value.semantics.ValueSemanticsAbstract;
import org.apache.causeway.schema.common.v2.ValueType;

import lombok.val;

@Profile("demo-jpa")
//tag::class[]
@Named("demo.ComplexNumberJpaValueSemantics")
@Component
public class ComplexNumberJpaValueSemantics
        extends ValueSemanticsAbstract<ComplexNumberJpa> {
    // ...
//end::class[]

    @Override
    public Class<ComplexNumberJpa> getCorrespondingClass() {
        return ComplexNumberJpa.class;
    }

    @Override
    public ValueType getSchemaValueType() {
        return ValueType.COMPOSITE;
    }

//tag::compose[]
    @Override
    public ValueDecomposition decompose(final ComplexNumberJpa value) {
        return CommonDtoUtils.typedTupleBuilder(value)
                .addFundamentalType(ValueType.DOUBLE, "re", ComplexNumberJpa::getRe)
                .addFundamentalType(ValueType.DOUBLE, "im", ComplexNumberJpa::getIm)
                .buildAsDecomposition();
    }

    @Override
    public ComplexNumberJpa compose(final ValueDecomposition decomposition) {
        return decomposition.right()
                .map(CommonDtoUtils::typedTupleAsMap)
                .map(map->ComplexNumberJpa.of(
                        (Double)map.get("re"),
                        (Double)map.get("im")))
                .orElse(null);
    }
//end::compose[]

//tag::getRenderer[]
    @Override
    public Renderer<ComplexNumberJpa> getRenderer() {
        return (context, object) -> title(object, "NaN");
    }

    private static String title(final ComplexNumberJpa complexNumber, final String fallbackIfNull) {
        if (complexNumber == null) return fallbackIfNull;
        return complexNumber.getRe() +
                (complexNumber.getIm() >= 0
                        ? (" + " +  complexNumber.getIm())
                        : (" - " + (-complexNumber.getIm())))
                + "i";
    }
//end::getRenderer[]

//tag::getParser[]
    @Override
    public Parser<ComplexNumberJpa> getParser() {
        return new Parser<ComplexNumberJpa>() {
            @Override
            public String parseableTextRepresentation(final Context context, final ComplexNumberJpa value) {
                return title(value, "NaN");
            }

            @Override
            public ComplexNumberJpa parseTextRepresentation(final Context context, final String complexNumberString) {
                if(!org.springframework.util.StringUtils.hasLength(complexNumberString)
                        || complexNumberString.contains("NaN")) {
                    return null;
                }
                // this is a naive implementation, just for demo
                final String[] parts = complexNumberString.split("\\+|i");
                val real = Double.parseDouble(parts[0]);
                val imaginary = Double.parseDouble(parts[1]);
                return ComplexNumberJpa.of(real, imaginary);
            }

            @Override
            public int typicalLength() {
                return 20;
            }
        };
    }
//end::getParser[]

//tag::class[]
}
//end::class[]