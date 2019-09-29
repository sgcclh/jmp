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

package dev.castive.jmp.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.castive.jmp.db.dao.*
import dev.castive.jmp.db.source.HikariSource
import dev.castive.jmp.tasks.GroupsTask
import dev.castive.log2.Log
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseHelper(private val store: ConfigStore) {
    fun start() {
        Log.v(javaClass, "Database config: [${store.url}, ${store.driver}]")
        Log.v(javaClass, "Application config: [baseUrl: ${store.baseUrl}, dataPath: ${store.dataPath}]")
        val ds = HikariDataSource(HikariConfig().apply {
            jdbcUrl = store.url
            driverClassName = store.driver
            username = store.tableUser
            password = store.tablePassword
        })
        HikariSource().connect(ds)
        Runtime.getRuntime().addShutdownHook(Thread {
            Log.w(javaClass, "Running shutdown hook, DO NOT forcibly close the application")
            ds.close()
        })
        setup()
    }
    private fun setup() {
        transaction {
            // Ensure that the tables are created
            Log.i(javaClass, "Checking for database drift")
            SchemaUtils.createMissingTablesAndColumns(Jumps, Users, Roles, Groups, GroupUsers, Aliases, Sessions)
            Init(store.dataPath) // Ensure that the default admin/roles is created
        }
        // start the GroupsTask cron
        GroupsTask.start()
    }
}