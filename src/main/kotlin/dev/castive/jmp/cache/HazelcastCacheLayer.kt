package dev.castive.jmp.cache

import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import dev.castive.jmp.db.Util
import dev.castive.jmp.util.SystemUtil
import dev.castive.log2.Log
import java.util.*

class HazelcastCacheLayer: BaseCacheLayer {
	private lateinit var hzInstance: HazelcastInstance
	private lateinit var map: IMap<String, String>

	override fun setup(): Boolean {
		if(connected()) {
			Log.e(javaClass, "We already have an active Hazelcast instance")
			return false
		}
		Log.i(javaClass, "Hazelcast is starting...")
		hzInstance = Hazelcast.getOrCreateHazelcastInstance(Config("jmp-hz"))
		map = hzInstance.getMap("claimedUsers")
		Log.v(javaClass, "Hazelcast contains map with ${map.size} entries")
		return true
	}

	override fun tearDown(): Boolean {
		if(!connected()) {
			Log.e(javaClass, "There is no connected instance to shutdown")
			return false
		}
		Log.i(javaClass, "Hazelcast is being shut down...")
		hzInstance.lifecycleService.shutdown()
		return true
	}

	override fun connected(): Boolean {
		if(!this::hzInstance.isInitialized) {
			Log.e(javaClass, "Hazelcast is not initialised. Please run $javaClass::setup() first")
			return false
		}
		return hzInstance.lifecycleService.isRunning
	}

	override fun getUser(id: UUID, token: String): Pair<UUID, String>? {
		if(map.isEmpty) return null
		val guess = get(map[token] ?: return null)
		if(System.currentTimeMillis() - guess.time > 5000) return null
		if(guess.id == id) return Pair(guess.id, token)
		return null
	}

	override fun getUser(token: String): UUID? {
		if(map.isEmpty) return null
		val id = map[token] ?: return null
		return Util.getSafeUUID(id)
	}

	override fun setUser(id: UUID, token: String) {
		map[token] = set(BaseCacheLayer.UserCache(id, System.currentTimeMillis()))
	}

	override fun removeUser(token: String) {
		map.remove(token)
	}

	private fun get(str: String): BaseCacheLayer.UserCache = SystemUtil.gson.fromJson(str, BaseCacheLayer.UserCache::class.java)
	private fun set(uc: BaseCacheLayer.UserCache) = SystemUtil.gson.toJson(uc)
}