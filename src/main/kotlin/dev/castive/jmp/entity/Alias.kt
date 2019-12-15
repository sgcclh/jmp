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

package dev.castive.jmp.entity

import dev.castive.log2.logv
import javax.persistence.*

@Entity
@Table(name = "Aliases")
data class Alias(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Int,
	var name: String,
	val parent: Int
) {

	@PreRemove
	fun onPreRemove() {
		"Removing ${javaClass.simpleName} with id: $id".logv(javaClass)
	}
}
