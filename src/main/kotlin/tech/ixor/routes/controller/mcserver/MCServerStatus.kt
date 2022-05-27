package tech.ixor.routes.controller.mcserver

import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import tech.ixor.entity.MinecraftServerEntity
import tech.ixor.entity.MinecraftServers

fun Route.mcServerStatus() {
    get("/mcserver/status") {
        val request = call.receive<MCServerStatusRequest>()
        val host = request.host
        val port = request.port
        val minecraftServer: MinecraftServerEntity? = MinecraftServers.getServer(host, port)
        if (minecraftServer != null) {
            call.respondText(minecraftServer.status().message)
        } else {
            call.respondText("Server not found", status = HttpStatusCode.NotFound)
        }
    }
}
