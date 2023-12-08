package com.universal.core.library.security;

import java.util.Arrays;

public class SecureString implements CharSequence,AutoCloseable {
    private final char[] chars;

    public SecureString(char[] chars){
        this.chars = new char[chars.length];
        System.arraycopy(chars, 0, this.chars, 0, chars.length);
    }

    public SecureString(char[] chars, int start, int end){
        this.chars = new char[end - start];
        System.arraycopy(chars, start, this.chars, 0, this.chars.length);
    }

    @Override
    public int length() {
        return chars.length;
    }

    @Override
    public char charAt(int index) {
        return chars[index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return new SecureString(this.chars, start, end);
    }

    public String asString() {
        final char[] value = new char[chars.length];
        for (int i = 0; i < value.length; i++) {
            value[i] = charAt(i);
        }
        return new String(value);
    }


    /**
     * Manually clear the underlying array holding the characters
     */
    public void clear(){
        Arrays.fill(chars, '0');
    }


    @Override
    public void close() throws Exception {
        clear();
    }
}