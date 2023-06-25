package io.github.haappi.duckpaper.commands;

import io.github.haappi.duckpaper.DuckPaper;

import static io.github.haappi.duckpaper.utils.CommandRelated.registerNewCommand;

public class CommandHandler {
    public CommandHandler(DuckPaper instance) {
        registerNewCommand(new Kiss("kiss"));
        registerNewCommand(new NightVision(instance));
    }
}
