/*
 *    Copyright 2019 Django Cass
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package dev.castive.jmp

import dev.castive.jmp.api.App
import dev.castive.jmp.db.DatabaseHelper
import dev.castive.jmp.util.EnvUtil
import dev.castive.jmp.util.checks.AuditCheck
import dev.castive.jmp.util.checks.EntropyCheck
import dev.castive.jmp.util.checks.FavCheck
import dev.castive.jmp.util.checks.JavaVersionCheck
import dev.castive.log2.logi
import dev.castive.log2.logv
import dev.castive.log2.logw
import dev.dcas.castive_utilities.extend.env
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class Runner {
    companion object {
        const val BASE = "/api"
        var START_TIME = 0L
    }
    private fun runInitialChecks() {
        "Checking security configuration".logi(javaClass)
        println("Running setup checks\n")
        val checks = arrayListOf(
            EntropyCheck(),
            AuditCheck(),
            JavaVersionCheck(),
            FavCheck()
        )
        var count = 0
        for (c in checks) {
            if(c.runCheck()) count++
        }
        println("Setup checks completed ($count/${checks.size} passed)\n")
    }
    fun start(args: Array<String>) = runBlocking {
        START_TIME = System.currentTimeMillis()
        args.contentToString().logv(javaClass)
        val arguments = Arguments(args)
        // Alert the user that dev features are enabled
        if(arguments.enableCors) "WARNING: CORS access is enabled for ALL origins. DO NOT allow this in production: WARNING".logw(javaClass)
        if(arguments.enableDev) "WARNING: Development mode is enabled".logw(javaClass)
        launch {
            runInitialChecks()
        }
        DatabaseHelper().start()
        // Start the application and wait for it to finish
        val appPort = EnvUtil.PORT.env("7000").toIntOrNull() ?: 7000
        launch { App(appPort).start(arguments) }.join()
    }
}

fun main(args: Array<String>) {
    Runner().start(args)
}
