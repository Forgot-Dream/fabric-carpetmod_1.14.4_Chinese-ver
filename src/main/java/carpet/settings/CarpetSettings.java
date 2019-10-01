package carpet.settings;

import carpet.CarpetServer;
import carpet.utils.Messenger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static carpet.settings.RuleCategory.BUGFIX;
import static carpet.settings.RuleCategory.COMMAND;
import static carpet.settings.RuleCategory.CREATIVE;
import static carpet.settings.RuleCategory.EXPERIMENTAL;
import static carpet.settings.RuleCategory.FEATURE;
import static carpet.settings.RuleCategory.OPTIMIZATION;
import static carpet.settings.RuleCategory.SURVIVAL;

public class CarpetSettings
{
    public static final String carpetVersion = "1.1.0+v190927汉化：闪电小绿[Bilibili]&LinXin、忘却梦空[Bilibili]";
    public static final Logger LOG = LogManager.getLogger();
    public static boolean skipGenerationChecks = false;
    public static boolean impendingFillSkipUpdates = false;
    public static Box currentTelepotingEntityBox = null;
    public static Vec3d fixedPosition = null;

    @Rule(
            desc = "下界传送门正确地传送实体",
            extra = "实体将不会卡在黑曜石里面",
            category = BUGFIX
    )
    public static boolean portalSuffocationFix = false;

    @Rule(desc = "Gbhs sgnf sadsgras fhskdpri!#我也看不懂", category = EXPERIMENTAL)
    public static boolean superSecretSetting = false;

    @Rule(
            desc = "创造模式的玩家将不会立即被地狱门传送",
            extra = "如果玩家双手拿着黑曜石，那么他/她将不被传送",
            category = CREATIVE
    )
    public static boolean portalCreativeDelay = false;

    @Rule(desc = "在使用工作台时按住Crtl+Q可以扔出合成产物", category = {BUGFIX, SURVIVAL})
    public static boolean ctrlQCraftingFix = false;

    @Rule(desc = "鹦鹉只会在玩家受到伤害后才会站在玩家肩膀上", category = {SURVIVAL, FEATURE})
    public static boolean persistentParrots = false;

    /*@Rule(
            desc = "Mobs growing up won't glitch into walls or go through fences",
            category = BUGFIX,
            validate = Validator.WIP.class
    )
    public static boolean growingUpWallJump = false;

    @Rule(
            desc = "Won't let mobs glitch into blocks when reloaded.",
            extra = "Can cause slight differences in mobs behaviour",
            category = {BUGFIX, EXPERIMENTAL},
            validate = Validator.WIP.class
    )
    public static boolean reloadSuffocationFix = false;
    */

    @Rule( desc = "玩家将没有延时地吸收经验球", category = CREATIVE )
    public static boolean xpNoCooldown = false;

    @Rule( desc = "小的经验球自动合并成大的经验球", category = FEATURE )
    public static boolean combineXPOrbs = false;

    @Rule(
            desc = "相同颜色空潜影盒掉落物将合并成组",
            extra = "按住shift以在物品界面中移动堆叠潜影盒",
            category = {SURVIVAL, FEATURE}
    )
    public static boolean stackableShulkerBoxes = false;

    @Rule( desc = "任何类型爆炸不破坏方块", category = CREATIVE )
    public static boolean explosionNoBlockDamage = false;

    @Rule( desc = "TNT在激活时不会随机位移", category = CREATIVE )
    public static boolean tntPrimerMomentumRemoved = false;

    @Rule(
            desc = "红石粉卡顿优化",
            extra = "由Theosib贡献代码",
            category = {EXPERIMENTAL, OPTIMIZATION}
    )
    public static boolean fastRedstoneDust = false;

    @Rule(desc = "沙漠神殿中只生成尸壳", category = FEATURE)
    public static boolean huskSpawningInTemples = false;

    @Rule( desc = "末地城作为蛤（潜影贝）的生成区域", category = FEATURE )
    public static boolean shulkerSpawningInEndCities = false;

    @Rule(desc = "实体进入未加载区块时不再消失", category = {EXPERIMENTAL, BUGFIX})
    public static boolean unloadedEntityFix = false;

    @Rule( desc = "TNT被放置在信号源边上时不被立即激活", category = CREATIVE )
    public static boolean tntDoNotUpdate = false;

    @Rule(
            desc = "玩家移动过快时不再回弹",
            extra = {"服务端啊，请相信客户端的移动判定吧！"},
            category = {CREATIVE, SURVIVAL}
    )
    public static boolean antiCheatDisabled = false;

    @Rule(desc = "活塞，投掷器，发射器的半连接性（BUD位）设成false即为关闭", category = CREATIVE)
    public static boolean quasiConnectivity = true;

    @Rule(
            desc = "使用仙人掌来旋转/翻转方块",
            extra = {
                    "方块被旋转/翻转时不会造成方块更新",
                    "适用于活塞，侦测器，中继器，比较器，发射器，比较器，楼梯等等"
            },
            category = {CREATIVE, SURVIVAL, FEATURE}
    )
    public static boolean flippinCactus = false;

    @Rule(
            desc = "指向羊毛的漏斗将会记录经过牠们的的物品",
            extra = {
                    "Enables /counter command, and actions while placing red and green carpets on wool blocks",
                    "Use /counter <color?> reset to reset the counter, and /counter <color?> to query",
                    "In survival, place green carpet on same color wool to query, red to reset the counters",
                    "Counters are global and shared between players, 16 channels available",
                    "Items counted are destroyed, count up to one stack per tick per hopper"
            },
            category = {COMMAND, CREATIVE, FEATURE}
    )
    public static boolean hopperCounters = false;

    @Rule( desc = "守卫者受到雷击时将会变成远古守卫者", category = FEATURE )
    public static boolean renewableSponges = false;

    @Rule( desc = "活塞可推动容器, 例如箱子、漏斗等.", category = {EXPERIMENTAL, FEATURE} )
    public static boolean movableBlockEntities = false;

    @Rule( desc = "在炎热群系（如沙漠、平地山等）且旁边没有水源时，树苗会变成枯死的灌木", category = FEATURE )
    public static boolean desertShrubs = false;

    @Rule( desc = "蠹虫钻出方块时掉落沙砾", category = FEATURE )
    public static boolean silverFishDropGravel = false;

    @Rule( desc = "使人工生成的雷电拥有自然生成雷电的特性", category = CREATIVE )
    public static boolean summonNaturalLightning = false;

    @Rule(desc = "允许/spawn命令追踪生物生成", category = COMMAND)
    public static boolean commandSpawn = true;

    @Rule(desc = "允许/tick命令修改游戏时间流速", category = COMMAND)
    public static boolean commandTick = true;

    @Rule(
            desc = "允许/log命令在聊天栏或者Tab界面查看信息",
            category = COMMAND,
            validate = Validator.WIP.class
    )
    public static boolean commandLog = true;

    @Rule(
            desc = "为进入新玩家设置这些功能为开启",
            extra = "比如tps,mobcaps等",
            category = {CREATIVE, SURVIVAL},
            options = {"none", "tps", "mobcaps,tps"}
    )
    public static String defaultLoggers = "none";

    @Rule(
            desc = "允许/distance命令查看两点间距离",
            extra = "如果carpet规则打开，那么也可以通过放置棕色地毯实现此命令",
            category = COMMAND
    )
    public static boolean commandDistance = true;

    @Rule(
            desc = "允许/info命令查看方块信息",
            extra = {
                    "如果carpet规则打开",
                    "也可以放置灰色地毯实现此命令"
            },
            category = COMMAND
    )
    public static boolean commandInfo = true;

    @Rule(
            desc = "允许/c和/s命令快速切换生存模式与旁观者模式",
            extra = "无论玩家是否管理员，都可以使用/c和/s命令",
            category = COMMAND
    )
    public static boolean commandCameramode = true;

    @Rule(
            desc = "允许/perimeterinfo命令",
            extra = "...扫描区域周围的区域以寻找潜在的可生成区域[机翻]",
            category = COMMAND
    )
    public static boolean commandPerimeterInfo = true;

    @Rule(desc = "启用/draw命令", extra = "... 以绘制简单图形", category = COMMAND)
    public static boolean commandDraw = true;

    @Rule(desc = "启用/script命令", extra = "它是一个游戏内置的供Scarpet脚本运行的API", category = COMMAND)
    public static boolean commandScript = true;

    @Rule(
            desc = "世界文件中的Scarpet脚本将在服务器/世界启动时自动加载",
            extra = "/script需要开启",
            category = CREATIVE
    )
    public static boolean scriptsAutoload = false;

    @Rule(desc = "允许/player命令以生成/控制假人", category = COMMAND)
    public static boolean commandPlayer = true;

    @Rule(desc = "允许通过/track命令跟踪生物AI", category = COMMAND)
    public static boolean commandTrackAI = true;

    @Rule(desc = "允许非管理员玩家使用地毯来控制carpet设置", category = SURVIVAL)
    public static boolean carpets = false;

    @Rule(desc = "活塞、玻璃、海绵将被合适的工具更快地破坏[瞬间破坏]", category = SURVIVAL)
    public static boolean missingTools = false;

    @Rule(desc = "增强地狱门的缓存机制以减小卡顿", category = {SURVIVAL, CREATIVE})
    public static boolean portalCaching = false;

    @Rule(desc = "使用/fill、/setblock、/clone命令以及结构方块时将造成方块更新", category = CREATIVE)
    public static boolean fillUpdates = true;

    @Rule(desc = "TPS设置较低时，客户端画面会更加顺滑", extra = "仅仅对本玩家起作用, 并且会降低客户端运行速度", category = CREATIVE)
    public static boolean smoothClientAnimations;

    @Rule(
            desc="通过信任客户端的挖掘动作，以修复挖掘产生的幽灵方块",
            extra="顺便把玩家的挖掘允许距离增加到32格（并无卵用）",
            category = SURVIVAL
    )
    public static boolean miningGhostBlockFix;

    private static class PushLimitLimits extends Validator<Integer> {
        @Override public Integer validate(ServerCommandSource source, ParsedRule<Integer> currentRule, Integer newValue, String string) {
            return (newValue>0 && newValue <= 1024) ? newValue : null;
        }
        @Override
        public String description() { return "你必须从1到1024中选取一个数值";}
    }
    @Rule(
            desc = "调整活塞推动方块最大数量",
            options = {"10", "12", "14", "100"},
            category = CREATIVE,
            validate = PushLimitLimits.class
    )
    public static int pushLimit = 12;

    @Rule(
            desc = "调整充能铁轨/激活铁轨的最大激活长度",
            options = {"9", "15", "30"},
            category = CREATIVE,
            validate = PushLimitLimits.class
    )
    public static int railPowerLimit = 9;

    private static class FillLimitLimits extends Validator<Integer> {
        @Override public Integer validate(ServerCommandSource source, ParsedRule<Integer> currentRule, Integer newValue, String string) {
            return (newValue>0 && newValue < 20000000) ? newValue : null;
        }
        @Override
        public String description() { return "你必须从1到20000000中选取一个数值";}
    }
    @Rule(
            desc = "调整/fill和/clone最大填充方块数量",
            options = {"32768", "250000", "1000000"},
            category = CREATIVE,
            validate = FillLimitLimits.class
    )
    public static int fillLimit = 32768;

    @Rule(
            desc = "调整实体碰撞最大限制，0为无限制",
            options = {"0", "1", "20"},
            category = OPTIMIZATION,
            validate = Validator.NONNEGATIVE_NUMBER.class
    )
    public static int maxEntityCollisions = 0;

    /*
    @Rule(
            desc = "Fix for piston ghost blocks",
            category = BUGFIX,
            validate = Validator.WIP.class
    )
    public static boolean pistonGhostBlocksFix = true;

    @Rule(
            desc = "fixes water performance issues",
            category = OPTIMIZATION,
            validate = Validator.WIP.class
    )
    public static boolean waterFlow = true;
    */

    @Rule(desc = "只需一个玩家睡觉就能在服务器中跳过夜晚", category = SURVIVAL)
    public static boolean onePlayerSleeping = false;

    @Rule(
            desc = "设置不同客户端连接到服务器时的MOTD信息",
            extra = "输入“_”时显示默认配置文件中MOTD信息",
            options = "_",
            category = CREATIVE
    )
    public static String customMOTD = "_";

    @Rule(desc = "发射器使用仙人掌以旋转/翻转方块", extra = "默认以逆时针旋转", category = FEATURE)
    public static boolean rotatorBlock = false;

    private static class ViewDistanceValidator extends Validator<Integer>
    {
        @Override public Integer validate(ServerCommandSource source, ParsedRule<Integer> currentRule, Integer newValue, String string)
        {
            if (currentRule.get().equals(newValue))
            {
                return newValue;
            }
            if (newValue < 0 || newValue > 32)
            {
                Messenger.m(source, "r 视距必须为0到32之间的一个数值");
                return null;
            }
            MinecraftServer server = source.getMinecraftServer();

            if (server.isDedicated())
            {
                int vd = (newValue >= 2)?newValue:((DedicatedServer) server).getProperties().viewDistance;
                if (vd != CarpetServer.minecraft_server.getPlayerManager().getViewDistance())
                    CarpetServer.minecraft_server.getPlayerManager().setViewDistance(vd);
                return newValue;
            }
            else
            {
                Messenger.m(source, "r 视距只能在服务器上被调整");
                return 0;
            }
        }
        @Override
        public String description() { return "你必须选择0到32之间的一个数值 0为默认数值";}
    }
    @Rule(
            desc = "调整服务器视距",
            extra = "设置为0不会覆盖服务器设置中的值",
            options = {"0", "12", "16", "32"},
            category = CREATIVE,
            validate = ViewDistanceValidator.class
    )
    public static int viewDistance = 0;

    private static class DisableSpawnChunksValidator extends Validator<Boolean> {
        @Override public Boolean validate(ServerCommandSource source, ParsedRule<Boolean> currentRule, Boolean newValue, String string) {
            if (currentRule.get().booleanValue() == newValue.booleanValue())
            {
                //must been some startup thing
                return newValue;
            }
            ServerWorld overworld = CarpetServer.minecraft_server.getWorld(DimensionType.OVERWORLD);
            if (overworld != null) {
                ChunkPos centerChunk = new ChunkPos(new BlockPos(
                        overworld.getLevelProperties().getSpawnX(),
                        overworld.getLevelProperties().getSpawnY(),
                        overworld.getLevelProperties().getSpawnZ()
                ));
                ServerChunkManager chunkManager = (ServerChunkManager) overworld.getChunkManager();

                chunkManager.removeTicket(ChunkTicketType.START, centerChunk, 11, Unit.INSTANCE);
                if (!newValue)
                {
                    chunkManager.addTicket(ChunkTicketType.START, centerChunk, 11, Unit.INSTANCE);
                }
            }
            return newValue;
        }
    }
    @Rule(
            desc = "允许出生点区块卸载",
            category = CREATIVE,
            validate = DisableSpawnChunksValidator.class
    )
    public static boolean disableSpawnChunks = false;

    private static class KelpLimit extends Validator<Integer>
    {
        @Override public Integer validate(ServerCommandSource source, ParsedRule<Integer> currentRule, Integer newValue, String string) {
            return (newValue>=0 && newValue <=25)?newValue:null;
        }
        @Override
        public String description() { return "您必须在0到25之间选择一个值。25并且所有天然海带都可以生长25个块，选择0可使所有生成的海带不生长";}
    }
    @Rule(
            desc = "限制自然生成的海带长度",
            options = {"0", "2", "25"},
            category = FEATURE,
            validate = KelpLimit.class
    )
    public static int kelpGenerationGrowthLimit = 25;

    @Rule(desc = "使用骨粉催熟珊瑚枝生成珊瑚结构", category = FEATURE)
    public static boolean renewableCoral = false;

    @Rule(desc = "修复玩家在快速移动时放置的方块朝向会改变", category = BUGFIX)
    public static boolean placementRotationFix = false;

    @Rule(
            desc = "缰绳不再在未加载区块中断开/消失",
            extra = "眼见不为实，发现这个bug再次出现时试着重进游戏",
            category = BUGFIX
    )
    public static boolean leadFix = false;

    @Rule(
	        desc = "怪物生成需要更少的CPU和内存",
            extra = "已知bug:若有可以生成怪物的方块，且该方块上还有透明方块，怪物仍会生成在该方块上",
			category = OPTIMIZATION 
	)
    public static boolean lagFreeSpawning = false;

    @Rule(
            desc = "Prevents horses and other mobs to wander into the distance after dismounting",
            extra = "Fixes issues with various Joergens wandering off and disappearing client-side",
            category = BUGFIX
    )
    public static boolean horseWanderingFix = false;
}
