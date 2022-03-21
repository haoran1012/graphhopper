/*
 *  Licensed to GraphHopper GmbH under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for
 *  additional information regarding copyright ownership.
 *
 *  GraphHopper GmbH licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except in
 *  compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.graphhopper.routing.ev;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.graphhopper.storage.IntsRef;

import java.util.Arrays;

/**
 * This class allows to store distinct values via an enum. I.e. it stores just the indices
 */
public final class EnumEncodedValue<E extends Enum> extends IntEncodedValueImpl {
    public final Class<E> enumType;
    @JsonIgnore
    public E[] arr;

    public EnumEncodedValue(String name, Class<E> enumType) {
        this(name, enumType, false);
    }

    public EnumEncodedValue(String name, Class<E> enumType, boolean storeTwoDirections) {
        this(name, getBits(enumType.getEnumConstants().length), 0, (1 << getBits(enumType.getEnumConstants().length)) - 1, false, storeTwoDirections, enumType);
    }

    private static int getBits(int length) {
        return 32 - Integer.numberOfLeadingZeros(length - 1);
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public EnumEncodedValue(@JsonProperty("name") String name,
                            @JsonProperty("bits") int bits,
                            @JsonProperty("minValue") int minValue,
                            @JsonProperty("maxValue") int maxValue,
                            @JsonProperty("negateReverseDirection") boolean negateReverseDirection,
                            @JsonProperty("storeTwoDirections") boolean storeTwoDirections,
                            @JsonProperty("enumType") Class<E> enumType) {
        super(name, bits, minValue, maxValue, negateReverseDirection, storeTwoDirections);
        this.enumType = enumType;
        arr = enumType.getEnumConstants();
    }

    public E[] getValues() {
        return arr;
    }

    public final void setEnum(boolean reverse, IntsRef ref, E value) {
        int intValue = value.ordinal();
        super.setInt(reverse, ref, intValue);
    }

    public final E getEnum(boolean reverse, IntsRef ref) {
        int value = super.getInt(reverse, ref);
        return arr[value];
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        EnumEncodedValue that = (EnumEncodedValue) o;
        return Arrays.equals(arr, that.arr);
    }

    @Override
    public int getVersion() {
        return 31 * super.getVersion() + staticHashCode(arr);
    }
}
