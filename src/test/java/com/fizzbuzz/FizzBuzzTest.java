package com.fizzbuzz;

import org.junit.Test;

import static java.lang.String.*;
import static org.assertj.core.api.Assertions.assertThat;

public class FizzBuzzTest {

    private FizzBuzzService service = new FizzBuzzService();

    @Test
    public void should_return_same_number() {
        // given
        int aNumber = 2;

        // when
        String result = service.guess(aNumber);

        // then
        assertThat(result).isEqualTo(valueOf(aNumber));
    }

    @Test
    public void should_return_fizz_when_multiple_3() {
        // given
        final int aNumber = 3;

        // when
        final String result = service.guess(aNumber);

        // then
        assertThat(result).isEqualTo("fizz");
    }

    @Test
    public void should_return_buzz_when_multiple_5() {
        // given
        final int aNumber = 5;

        // when
        final String result = service.guess(aNumber);

        // then
        assertThat(result).isEqualTo("buzz");
    }

    @Test
    public void should_return_fizzbuzz_when_multiple_of_3_and_5() {
        // given
        final int aNumber = 15;

        // when
        final String result = service.guess(aNumber);

        // then
        assertThat(result).isEqualTo("fizzbuzz");
    }

}
