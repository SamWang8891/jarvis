package com.jarvis

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val jda = JDABuilder.createDefault("----------").build()
    jda.awaitReady()
    jda.openPrivateChannelById(870506955515502636).queue {
        it.sendMessage("Hello world!").queue()
    }
    jda.addEventListener(Listener())
    jda.getGuildById("1458715348873515181")!!.updateCommands().addCommands(
        Commands.slash("test","command for test")
    ).queue()
}
class Listener : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if(event.name == "test"){
            event.deferReply(true).queue()
            event.hook.editOriginal("finished!").queue()
        }
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if(event.message.contentRaw == "hi" && !event.author.isBot){
            event.channel.sendMessage("Hello!").queue()

        }
    }
}