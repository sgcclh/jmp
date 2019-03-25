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

package dev.castive.jmp.api.v2

import dev.castive.jmp.api.Runner
import dev.castive.jmp.api.actions.InfoAction
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.EndpointGroup
import io.javalin.security.SecurityUtil.roles
import org.eclipse.jetty.http.HttpStatus

class Info: EndpointGroup {
    override fun addEndpoints() {
        // Version/info
        get("${Runner.BASE}/v2/version", { ctx ->
            ctx.status(HttpStatus.OK_200).result("v${dev.castive.jmp.Version.getVersion()}")
        }, dev.castive.jmp.api.Auth.defaultRoleAccess)
        get("${Runner.BASE}/v2/info/system", { ctx ->
            ctx.status(HttpStatus.OK_200).json(InfoAction().getSystem())
        }, roles(dev.castive.jmp.api.Auth.BasicRoles.ADMIN))
        get("${Runner.BASE}/v2/info/app", { ctx ->
            ctx.status(HttpStatus.OK_200).json(InfoAction().getApp())
        }, roles(dev.castive.jmp.api.Auth.BasicRoles.ADMIN))
    }
}