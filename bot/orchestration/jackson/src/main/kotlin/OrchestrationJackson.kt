/*
 * Copyright (C) 2017/2020 e-voyageurs technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ai.tock.bot.orchestration

import ai.tock.bot.orchestration.shared.OrchestrationData
import ai.tock.bot.orchestration.shared.OrchestrationSentence
import ai.tock.bot.orchestration.shared.SecondaryBotAction
import ai.tock.bot.orchestration.shared.SecondaryBotAvailableResponse
import ai.tock.bot.orchestration.shared.SecondaryBotEligibilityResponse
import ai.tock.bot.orchestration.shared.SecondaryBotNoResponse
import ai.tock.bot.orchestration.shared.SecondaryBotResponse
import ai.tock.bot.orchestration.shared.SecondaryBotSendChoice
import ai.tock.bot.orchestration.shared.SecondaryBotSendSentence
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.jsontype.NamedType
import com.fasterxml.jackson.databind.module.SimpleModule
import org.litote.jackson.JacksonModuleServiceLoader

private object OrchestrationJacksonConfiguration {

    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
    )
    interface MixinOrchestrationData

    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
    )
    interface MixinSecondaryBotAction

    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
    )
    interface MixinSecondaryBotResponse

    val module: SimpleModule
        get() {
            val module = SimpleModule()
            with(module) {

                setMixInAnnotation(OrchestrationData::class.java, MixinOrchestrationData::class.java)
                registerSubtypes(NamedType(OrchestrationSentence::class.java, "sentence"))

                setMixInAnnotation(SecondaryBotAction::class.java, MixinSecondaryBotAction::class.java)
                registerSubtypes(NamedType(SecondaryBotSendSentence::class.java, "sendSentence"))
                registerSubtypes(NamedType(SecondaryBotSendChoice::class.java, "sendChoice"))

                setMixInAnnotation(SecondaryBotResponse::class.java, MixinSecondaryBotResponse::class.java)
                registerSubtypes(NamedType(SecondaryBotAvailableResponse::class.java, "availableResponse"))
                registerSubtypes(NamedType(SecondaryBotEligibilityResponse::class.java, "eligibleResponse"))
                registerSubtypes(NamedType(SecondaryBotNoResponse::class.java, "noResponse"))

            }
            return module
        }

}

class OrchestrationJacksonModuleServiceLoader : JacksonModuleServiceLoader {
    override fun module(): Module = OrchestrationJacksonConfiguration.module
}