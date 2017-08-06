/*
 * Copyright (C) 2017 VSCT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.vsct.tock.bot.engine

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.provider
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import fr.vsct.tock.bot.admin.bot.BotApplicationConfigurationDAO
import fr.vsct.tock.bot.connector.Connector
import fr.vsct.tock.bot.connector.ConnectorType
import fr.vsct.tock.bot.engine.action.Action
import fr.vsct.tock.bot.engine.dialog.Dialog
import fr.vsct.tock.bot.engine.dialog.Story
import fr.vsct.tock.bot.engine.message.Message
import fr.vsct.tock.bot.engine.message.Sentence
import fr.vsct.tock.bot.engine.nlp.NlpController
import fr.vsct.tock.bot.engine.user.PlayerId
import fr.vsct.tock.bot.engine.user.PlayerType
import fr.vsct.tock.bot.engine.user.UserLock
import fr.vsct.tock.bot.engine.user.UserPreferences
import fr.vsct.tock.bot.engine.user.UserTimeline
import fr.vsct.tock.bot.engine.user.UserTimelineDAO
import fr.vsct.tock.nlp.api.client.NlpClient
import fr.vsct.tock.nlp.api.client.model.NlpResult
import fr.vsct.tock.shared.Executor
import fr.vsct.tock.shared.injector
import fr.vsct.tock.shared.tockInternalInjector
import fr.vsct.tock.translator.I18nDAO
import fr.vsct.tock.translator.TranslatorEngine
import org.junit.Before
import retrofit2.Response

/**
 *
 */
abstract class BotEngineTest {

    val userLock: UserLock = mock()
    val userTimelineDAO: UserTimelineDAO = mock()
    val userId = PlayerId("id")
    val botId = PlayerId("bot", PlayerType.bot)
    val botDefinition = BotDefinitionTest()
    val dialog = Dialog(setOf(userId, botId))
    val story = Story(botDefinition.stories.first(), testIntent)

    val botConfDAO: BotApplicationConfigurationDAO = mock()
    val i18nDAO: I18nDAO = mock()
    val translator: TranslatorEngine = mock {
        on { translate(any(), any(), any()) } doReturn ("ok")
    }

    val nlpClient: NlpClient = mock() {
        on { parse(any()) } doReturn (
                Response.success(
                        NlpResult(testIntent.name,
                                "test",
                                emptyList(),
                                1.0,
                                1.0,
                                "",
                                emptyMap())))
        on { parseIntentEntities(any()) } doReturn (
                Response.success(
                        NlpResult(testIntent.name,
                                "test",
                                emptyList(),
                                1.0,
                                1.0,
                                "",
                                emptyMap())))
    }
    val nlp: NlpController = mock()
    val executor: Executor = mock()
    val connector: Connector = mock {
        on { loadProfile(any(), any()) } doReturn (UserPreferences())
        on { connectorType } doReturn (ConnectorType("test", asynchronous = false))
    }

    val userTimeline = UserTimeline(userId)

    var userAction = action(Sentence("ok computer"))

    open fun baseModule(): Kodein.Module {
        return Kodein.Module {
            bind<NlpClient>() with provider { nlpClient }
            bind<NlpController>() with provider { nlp }
            bind<Executor>() with provider { executor }
            bind<UserLock>() with provider { userLock }
            bind<UserTimelineDAO>() with provider { userTimelineDAO }
            bind<I18nDAO>() with provider { i18nDAO }
            bind<TranslatorEngine>() with provider { translator }
            bind<BotApplicationConfigurationDAO>() with provider { botConfDAO }
        }
    }

    @Before
    fun before() {
        tockInternalInjector = KodeinInjector()
        injector.inject(Kodein {
            import(baseModule())
        })
    }

    fun action(message: Message): Action = message.toAction(userId, "applicationId", botId)

    val registeredBus: BotBus? get() = (story.definition as StoryDefinitionTest).registeredBus

    val bot: Bot by lazy {
        story.actions.add(userAction)
        dialog.stories.add(story)
        Bot(botDefinition)
    }
    val connectorController: ConnectorController by lazy { ConnectorController(bot, connector, BotVerticle()) }

}