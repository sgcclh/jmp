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

package com.django.jmp.api

import com.django.jmp.db.dao.Jump
import com.django.jmp.db.dao.JumpData
import info.debatty.java.stringsimilarity.JaroWinkler
import org.jetbrains.exposed.sql.transactions.transaction

class Similar(private val query: String, private val dict: ArrayList<Jump>, private val threshold: Double = 0.75) {
    private val results = arrayListOf<Jump>()
    fun compute() {
        transaction {
            val jw = JaroWinkler()
            results.clear()
            var best: Jump? = null
            var bestIndex = 0.0
            for (s in dict) {
                val metric = jw.similarity(query, s.name)
                if (metric > threshold)
                    results.add(s)
                if (metric > 0.65 && metric > bestIndex) {
                    bestIndex = metric
                    best = s
                }
            }
            if (results.size == 0 && best != null) {
                results.clear()
                results.add(best)
            }
        }
    }
    fun get(): ArrayList<JumpData> {
        val jumps = arrayListOf<JumpData>()
        transaction { results.forEach { jumps.add(JumpData(it)) } }
        return jumps
    }
}