package carpet.commands;

import carpet.helpers.EntityPlayerActionPack;
import carpet.settings.CarpetSettings;
import carpet.fakes.ServerPlayerEntityInterface;
import carpet.patches.EntityPlayerMPFake;
import carpet.utils.Messenger;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.DimensionArgumentType;
import net.minecraft.command.arguments.RotationArgumentType;
import net.minecraft.command.arguments.Vec3ArgumentType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.dimension.DimensionType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandSource.suggestMatching;

public class PlayerCommand
{

    // TODO: allow any order like execute
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        LiteralArgumentBuilder<ServerCommandSource> literalargumentbuilder = literal("player")
                .requires((player) -> CarpetSettings.commandPlayer)
                .then(argument("player", StringArgumentType.word())
                        .suggests( (c, b) -> suggestMatching(getPlayers(c.getSource()), b))
                        .then(literal("stop").executes(PlayerCommand::stop))
                        .then(makeActionCommand("use", EntityPlayerActionPack.ActionType.USE))
                        .then(makeActionCommand("jump", EntityPlayerActionPack.ActionType.JUMP))
                        .then(makeActionCommand("attack", EntityPlayerActionPack.ActionType.ATTACK))
                        .then(makeActionCommand("drop", EntityPlayerActionPack.ActionType.DROP_ITEM))
                        .then(makeActionCommand("dropStack", EntityPlayerActionPack.ActionType.DROP_STACK))
                        .then(makeActionCommand("swapHands", EntityPlayerActionPack.ActionType.SWAP_HANDS))
                        .then(literal("kill").executes(PlayerCommand::kill))
                        .then(literal("shadow"). executes(PlayerCommand::shadow))
                        .then(literal("mount").executes(manipulation(EntityPlayerActionPack::mount)))
                        .then(literal("dismount").executes(manipulation(EntityPlayerActionPack::dismount)))
                        .then(literal("sneak").executes(manipulation(ap -> ap.setSneaking(true))))
                        .then(literal("unsneak").executes(manipulation(ap -> ap.setSneaking(false))))
                        .then(literal("sprint").executes(manipulation(ap -> ap.setSprinting(true))))
                        .then(literal("unsprint").executes(manipulation(ap -> ap.setSprinting(false))))
                        .then(literal("look")
                                .then(literal("north").executes(manipulation(ap -> ap.look(Direction.NORTH))))
                                .then(literal("south").executes(manipulation(ap -> ap.look(Direction.SOUTH))))
                                .then(literal("east").executes(manipulation(ap -> ap.look(Direction.EAST))))
                                .then(literal("west").executes(manipulation(ap -> ap.look(Direction.WEST))))
                                .then(literal("up").executes(manipulation(ap -> ap.look(Direction.UP))))
                                .then(literal("down").executes(manipulation(ap -> ap.look(Direction.DOWN))))
                                .then(argument("direction", RotationArgumentType.rotation())
                                        .executes(c -> manipulate(c, ap -> ap.look(RotationArgumentType.getRotation(c, "direction").toAbsoluteRotation(c.getSource())))))
                        ).then(literal("turn")
                                .then(literal("left").executes(c -> manipulate(c, ap -> ap.turn(-90, 0))))
                                .then(literal("right").executes(c -> manipulate(c, ap -> ap.turn(90, 0))))
                                .then(literal("back").executes(c -> manipulate(c, ap -> ap.turn(180, 0))))
                                .then(argument("rotation", RotationArgumentType.rotation())
                                        .executes(c -> manipulate(c, ap -> ap.turn(RotationArgumentType.getRotation(c, "rotation").toAbsoluteRotation(c.getSource())))))
                        ).then(literal("move")
                                .then(literal("forward").executes(c -> manipulate(c, ap -> ap.setForward(1))))
                                .then(literal("backward").executes(c -> manipulate(c, ap -> ap.setForward(-1))))
                                .then(literal("left").executes(c -> manipulate(c, ap -> ap.setStrafing(1))))
                                .then(literal("right").executes(c -> manipulate(c, ap -> ap.setStrafing(-1))))
                        ).then(literal("spawn").executes(PlayerCommand::spawn)
                                .then(literal("at").then(argument("position", Vec3ArgumentType.vec3()).executes(PlayerCommand::spawn)
                                        .then(literal("facing").then(argument("direction", RotationArgumentType.rotation()).executes(PlayerCommand::spawn)
                                                .then(literal("in").then(argument("dimension", DimensionArgumentType.dimension()).executes(PlayerCommand::spawn)))
                                        ))
                                ))
                        )
                );
        dispatcher.register(literalargumentbuilder);
    }

    private static LiteralArgumentBuilder<ServerCommandSource> makeActionCommand(String actionName, EntityPlayerActionPack.ActionType type)
    {
        return literal(actionName)
                .executes(c -> action(c, type, EntityPlayerActionPack.Action.once()))
                .then(literal("once").executes(c -> action(c, type, EntityPlayerActionPack.Action.once())))
                .then(literal("continuous").executes(c -> action(c, type, EntityPlayerActionPack.Action.continuous())))
                .then(literal("interval").then(argument("ticks", IntegerArgumentType.integer(2))
                        .executes(c -> action(c, type, EntityPlayerActionPack.Action.interval(IntegerArgumentType.getInteger(c, "ticks"))))));
    }

    private static Collection<String> getPlayers(ServerCommandSource source)
    {
        Set<String> players = Sets.newLinkedHashSet(Arrays.asList("Steve", "Alex"));
        players.addAll(source.getPlayerNames());
        return players;
    }

    private static ServerPlayerEntity getPlayer(CommandContext<ServerCommandSource> context)
    {
        String playerName = StringArgumentType.getString(context, "player");
        MinecraftServer server = context.getSource().getMinecraftServer();
        return server.getPlayerManager().getPlayer(playerName);
    }

    private static boolean cantManipulate(CommandContext<ServerCommandSource> context)
    {
        PlayerEntity player = getPlayer(context);
        if (player == null)
        {
            Messenger.m(context.getSource(), "r 只能操纵已经召唤的假人");
            return true;
        }
        PlayerEntity sendingPlayer;
        try
        {
            sendingPlayer = context.getSource().getPlayer();
        }
        catch (CommandSyntaxException e)
        {
            return false;
        }

        if (!context.getSource().getMinecraftServer().getPlayerManager().isOperator(sendingPlayer.getGameProfile()))
        {
            if (sendingPlayer != player && !(player instanceof EntityPlayerMPFake))
            {
                Messenger.m(context.getSource(), "r 非OP玩家无法控制其他真实玩家");
                return true;
            }
        }
        return false;
    }

    private static boolean cantReMove(CommandContext<ServerCommandSource> context)
    {
        if (cantManipulate(context)) return true;
        PlayerEntity player = getPlayer(context);
        if (player instanceof EntityPlayerMPFake) return false;
        Messenger.m(context.getSource(), "r 只能移动或杀死假人");
        return true;
    }

    private static boolean cantSpawn(CommandContext<ServerCommandSource> context)
    {
        String playerName = StringArgumentType.getString(context, "player");
        MinecraftServer server = context.getSource().getMinecraftServer();
        PlayerManager manager = server.getPlayerManager();
        PlayerEntity player = manager.getPlayer(playerName);
        if (player != null)
        {
            Messenger.m(context.getSource(), "r 玩家/假人 ", "rb " + playerName, "r  已经登陆/召唤");
            return true;
        }
        GameProfile profile = server.getUserCache().findByName(playerName);
        if (manager.getUserBanList().contains(profile))
        {
            Messenger.m(context.getSource(), "r 玩家/假人 ", "rb " + playerName, "r  被封禁了");
            return true;
        }
        if (manager.isWhitelistEnabled() && profile != null && manager.isWhitelisted(profile) && !context.getSource().hasPermissionLevel(2))
        {
            Messenger.m(context.getSource(), "r 白名单中的玩家只能被OP召唤");
            return true;
        }
        return false;
    }

    private static int kill(CommandContext<ServerCommandSource> context)
    {
        if (cantReMove(context)) return 0;
        getPlayer(context).kill();
        return 1;
    }

    @FunctionalInterface
    interface SupplierWithCommandSyntaxException<T>
    {
        T get() throws CommandSyntaxException;
    }

    private static <T> T tryGetArg(SupplierWithCommandSyntaxException<T> a, SupplierWithCommandSyntaxException<T> b) throws CommandSyntaxException
    {
        try
        {
            return a.get();
        }
        catch (IllegalArgumentException e)
        {
            return b.get();
        }
    }

    private static int spawn(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        if (cantSpawn(context)) return 0;
        ServerCommandSource source = context.getSource();
        Vec3d pos = tryGetArg(
                () -> Vec3ArgumentType.getVec3(context, "position"),
                source::getPosition
        );
        Vec2f facing = tryGetArg(
                () -> RotationArgumentType.getRotation(context, "direction").toAbsoluteRotation(context.getSource()),
                source::getRotation
        );
        DimensionType dim = tryGetArg(
                () -> DimensionArgumentType.getDimensionArgument(context, "dimension"),
                () -> source.getWorld().dimension.getType()
        );
        GameMode mode = GameMode.CREATIVE;
        try
        {
            ServerPlayerEntity player = context.getSource().getPlayer();
            mode = player.interactionManager.getGameMode();
        }
        catch (CommandSyntaxException ignored) {}
        String playerName = StringArgumentType.getString(context, "player");
        MinecraftServer server = source.getMinecraftServer();
        PlayerEntity player = EntityPlayerMPFake.createFake(playerName, server, pos.x, pos.y, pos.z, facing.y, facing.x, dim, mode);
        if (player == null)
        {
            Messenger.m(context.getSource(), "rb 玩家/假人 " + StringArgumentType.getString(context, "player") + " 不存在 " +
                    "所以无法在在线模式下生成. 使服务器离线登陆以生成不存在的假人");
        }
        return 1;
    }

    private static int stop(CommandContext<ServerCommandSource> context)
    {
        if (cantManipulate(context)) return 0;
        ServerPlayerEntity player = getPlayer(context);
        ((ServerPlayerEntityInterface) player).getActionPack().stop();
        return 1;
    }

    private static int manipulate(CommandContext<ServerCommandSource> context, Consumer<EntityPlayerActionPack> action)
    {
        if (cantManipulate(context)) return 0;
        ServerPlayerEntity player = getPlayer(context);
        action.accept(((ServerPlayerEntityInterface) player).getActionPack());
        return 1;
    }

    private static Command<ServerCommandSource> manipulation(Consumer<EntityPlayerActionPack> action)
    {
        return c -> manipulate(c, action);
    }

    private static int action(CommandContext<ServerCommandSource> context, EntityPlayerActionPack.ActionType type, EntityPlayerActionPack.Action action)
    {
        return manipulate(context, ap -> ap.start(type, action));
    }

    private static int shadow(CommandContext<ServerCommandSource> context)
    {
        ServerPlayerEntity player = getPlayer(context);
        if (player instanceof EntityPlayerMPFake)
        {
            Messenger.m(context.getSource(), "r 不能覆盖假人");
            return 0;
        }
        EntityPlayerMPFake.createShadow(player.server, player);
        return 1;
    }
}
