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
import dev.castive.jmp.db.Config
import dev.castive.jmp.db.ConfigStore
import dev.castive.jmp.db.DatabaseHelper
import dev.castive.jmp.util.EnvUtil
import dev.castive.jmp.util.checks.AuditCheck
import dev.castive.jmp.util.checks.EntropyCheck
import dev.castive.jmp.util.checks.JavaVersionCheck
import dev.castive.jmp.util.checks.SecureConfigCheck
import dev.castive.log2.Log
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*


class Runner {
    companion object {
        const val BASE = "/api"
        var START_TIME = 0L
    }
    private fun runInitialChecks(store: ConfigStore, arguments: Arguments) {
        Log.i(javaClass, "Checking security configuration")
        println("Running setup checks\n")
        val checks = arrayListOf(SecureConfigCheck(store.baseUrl, arguments), EntropyCheck(), AuditCheck(), JavaVersionCheck())
        var count = 0
        for (c in checks) {
            if(c.runCheck()) count++
        }
        println("Setup checks completed ($count/${checks.size} passed)\n")
    }
    fun start(args: Array<String>) = runBlocking {
        START_TIME = System.currentTimeMillis()
        Log.v(javaClass, Arrays.toString(args))
        val arguments = Arguments(args)
        // Alert the user that dev features are enabled
        if(arguments.enableCors) Log.w(javaClass, "WARNING: CORS access is enabled for ALL origins. DO NOT allow this in production: WARNING")
        if(arguments.enableDev) Log.w(javaClass, "WARNING: Development mode is enabled")
        val store = Config().loadEnv()
        launch {
            runInitialChecks(store, arguments)
        }
        DatabaseHelper().start(store)
        // Start the application and wait for it to finish
        val appPort = EnvUtil.getEnv(EnvUtil.PORT, "7000").toIntOrNull() ?: 7000
        launch { App(appPort).start(store, arguments) }.join()
    }
}

fun main(args: Array<String>) {
    Runner().start(args)
}