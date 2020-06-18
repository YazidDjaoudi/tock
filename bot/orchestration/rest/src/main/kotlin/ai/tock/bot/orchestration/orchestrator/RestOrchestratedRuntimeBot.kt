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

package ai.tock.bot.orchestration.orchestrator

import ai.tock.bot.engine.user.PlayerId
import ai.tock.bot.orchestration.shared.AskEligibilityToOrchestratedBotRequest
import ai.tock.bot.orchestration.shared.NoOrchestrationStatus
import ai.tock.bot.orchestration.shared.OrchestrationMetaData
import ai.tock.bot.orchestration.shared.OrchestrationTargetedBot
import ai.tock.bot.orchestration.shared.ResumeOrchestrationRequest
import ai.tock.bot.orchestration.shared.SecondaryBotNoResponse
import ai.tock.bot.orchestration.shared.SecondaryBotResponse
import ai.tock.shared.addJacksonConverter
import ai.tock.shared.create
import ai.tock.shared.retrofitBuilderWithTimeoutAndLogger
import mu.KotlinLogging
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

class RestOrchestratedRuntimeBot(
    target: OrchestrationTargetedBot,
    urlBot : String,
    timeoutMs : Long
) : OrchestratedRuntimeBot(target) {

    private val targetBotClient = BotRestClient.create(urlBot, timeoutMs)

    override fun askOrchestration(request: AskEligibilityToOrchestratedBotRequest) : SecondaryBotResponse {
        return targetBotClient.askOrchestration(request).execute().body() ?: SecondaryBotNoResponse(
            status = NoOrchestrationStatus.NOT_AVAILABLE,
            metaData = request.metadata ?: OrchestrationMetaData(PlayerId("unknown"), target.botId, PlayerId("orchestrator"))
        )
    }

    override fun resumeOrchestration(request: ResumeOrchestrationRequest) : SecondaryBotResponse {
        return targetBotClient.resumeOrchestration(request).execute().body() ?: SecondaryBotNoResponse(
            status = NoOrchestrationStatus.END,
            metaData = request.metadata
        )
    }

}

private interface BotRestClient {

    @POST("orchestration/eligibility")
    fun askOrchestration(@Body request: AskEligibilityToOrchestratedBotRequest): Call<SecondaryBotResponse>


    @POST("orchestration/proxy")
    fun resumeOrchestration(@Body request: ResumeOrchestrationRequest): Call<SecondaryBotResponse>

    companion object {

        private val logger = KotlinLogging.logger {}

        fun create(url: String, timeout: Long = 30000L): BotRestClient =
            retrofitBuilderWithTimeoutAndLogger(timeout, logger)
                    .addJacksonConverter()
                    .baseUrl(url)
                    .build()
                    .create()
    }
}