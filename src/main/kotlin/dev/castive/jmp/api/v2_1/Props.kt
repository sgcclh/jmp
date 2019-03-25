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

package dev.castive.jmp.api.v2_1

import dev.castive.jmp.api.Runner
import dev.castive.jmp.auth.Providers
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.EndpointGroup
import org.eclipse.jetty.http.HttpStatus

class Props(private val providers: Providers): EndpointGroup {
    override fun addEndpoints() {
        get("${Runner.BASE}/v2_1/prop/:target", { ctx ->
            val targetProp = ctx.pathParam("target")
            val result = when {
                providers.keyedProps.containsKey(targetProp) -> providers.keyedProps[targetProp]
                else -> providers.properties.getOrDefault(targetProp, "undefined")
            }
            ctx.status(HttpStatus.OK_200).result(result.toString())
        }, dev.castive.jmp.api.Auth.adminRoleAccess)
        get("${Runner.BASE}/v2_1/uprop/allow_local", { ctx ->
            val targetProp = "jmp.ext.allow_local"
            val result = when {
                providers.keyedProps.containsKey(targetProp) -> providers.keyedProps[targetProp]
                else -> providers.properties.getOrDefault(targetProp, "undefined")
            }
            ctx.status(HttpStatus.OK_200).result(result.toString())
        }, dev.castive.jmp.api.Auth.defaultRoleAccess)
    }
}