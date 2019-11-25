package ai.tock.bot.connector.messenger.json.webhook

import ai.tock.bot.connector.messenger.model.Recipient
import ai.tock.bot.connector.messenger.model.Sender
import ai.tock.bot.connector.messenger.model.webhook.CallbackRequest
import ai.tock.bot.connector.messenger.model.webhook.Entry
import ai.tock.bot.connector.messenger.model.webhook.Message
import ai.tock.bot.connector.messenger.model.webhook.MessageWebhook
import ai.tock.shared.jackson.mapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CallbackRequestDeserializationTest {

    @Test
    fun `standby is correctly deserialized`() {
        val r =
            CallbackRequest(
                "page",
                listOf(
                    Entry(
                        "PAGE_ID",
                        0,
                        standby = listOf(
                            MessageWebhook(Sender("1"), Recipient("2"), 1L, Message("aa", "text"))
                        )
                    )
                )
            )
        val s = mapper.writeValueAsString(r)
        assertEquals(r, mapper.readValue(s))
    }



    @Test
    fun testDeserialization() {
        val json ="""
        {
            "object": "page",
            "entry": [
                {
                    "id": "1072877829517101",
                    "time": 1574337626872,
                    "messaging": [
                        {
                            "sender": {
                                "id": "1696785613739982"
                            },
                            "recipient": {
                                "id": "1072877829517101"
                            },
                            "timestamp": 1574337624727,
                            "message": {
                                "mid": "m_bWelEDyH_VUB2asdGYWNskansOeR_J9rheTeWjyz7SOKVtTieg4N7cKiLIioB-p1jMGHO81pvH3M7g5zBOzppQ",
                                "text": "Bonjour",
                                "nlp": {
                                    "entities": {},
                                    "detected_locales": [
                                        {
                                            "locale": "fr_XX",
                                            "confidence": 0.9988
                                        }
                                    ]
                                }
                            }
                        }
                    ]
                }
            ]
        }
    """.trimIndent()
        println(mapper.readValue<CallbackRequest>(json))
    }
}