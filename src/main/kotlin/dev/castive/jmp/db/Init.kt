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

import dev.castive.javalin_auth.auth.Roles
import dev.castive.jmp.api.Auth
import dev.castive.jmp.db.dao.Role
import dev.castive.jmp.db.dao.User
import dev.castive.log2.Log
import dev.castive.log2.logok
import dev.castive.log2.logv
import dev.castive.securepass3.PasswordGenerator
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

class Init(private val dataPath: String) {
    init {
        val superName = "admin" // Hardcoded into FE, don't change
        transaction {
            addLogger(StdOutSqlLogger)
            // roles must be created before the admin
            if(Role.all().empty()) {
                for (r in Roles.BasicRoles.values()) {
                    Role.new {
                        name = r.name
                    }
                }
                "Initialised user roles".logok(javaClass)
            }
	        "Located ${User.all().count()} existing users...".logv(javaClass)
            if(User.all().empty()) {
                val password = PasswordGenerator().generate(32, strong = false) // Strong causes blocking issues in Docker
                Auth().createUser(superName, password, true, "Admin")
                Log.w(javaClass, "Created superuser with access: [username: $superName]\nPlease change this ASAP!\nThis will also be stored in the current directory in 'initialAdminPassword'")
                try {
	                var path = dataPath
	                if(!path.endsWith(File.separatorChar)) path += File.separatorChar
                    Files.writeString(Path.of("${path}initialAdminPassword"), String(password), StandardCharsets.UTF_8)
                }
                catch (e: Exception) {
                    Log.e(javaClass, "Failed to save admin password: $e")
                }
                for (c in password) // Probably useless if converted to a string above
                    print(c)
                println()
            }
        }
    }
}