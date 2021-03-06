/*
 * This file is part of IPLog, licensed under the MIT License.
 *
 * Copyright (c) 2017 Meronat <http://meronat.com>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.ichorpowered.iplog.command;

import com.ichorpowered.iplog.IPLog;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Optional;

public class AddCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        final Optional<User> optionalUser = args.getOne("player");

        if (!optionalUser.isPresent()) {
            throw new CommandException(Text.of(TextColors.RED, "You must specify an existing user."));
        }

        final Optional<InetAddress> optionalIP = args.getOne("ip");

        if (!optionalIP.isPresent()) {
            throw new CommandException(Text.of(TextColors.RED, "You must specify a proper IP address."));
        }

        final User user = optionalUser.get();
        final InetAddress ip = optionalIP.get();

        if (IPLog.getPlugin().getStorage().isPresent(ip, user.getUniqueId())) {
            throw new CommandException(Text.of(TextColors.RED, "This connection already exists in the database."));
        }

        Sponge.getScheduler().createAsyncExecutor(IPLog.getPlugin()).execute(() -> IPLog.getPlugin().getStorage().addConnection(ip, user.getUniqueId(), LocalDateTime.now()));

        src.sendMessage(Text.of(TextColors.YELLOW, "You have successfully added the specified connection to the database."));

        return CommandResult.success();
    }

}
