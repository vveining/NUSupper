package com.example.nusupper

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CreateJioTest {

    @Test
    fun `dateParse$app_debug`() {

        assertThat(CreateJio.dateParse(8,0,2999))
            .isEqualTo("08.01.2999")

        assertThat(CreateJio.dateParse(9,11,2390))
            .isEqualTo("09.12.2390")
    }

    @Test
    fun `timeParse$app_debug`() {

        assertThat(CreateJio.timeParse(0,0))
            .isEqualTo("00:00")

        assertThat(CreateJio.timeParse(13,1))
            .isEqualTo("13:01")

    }
}