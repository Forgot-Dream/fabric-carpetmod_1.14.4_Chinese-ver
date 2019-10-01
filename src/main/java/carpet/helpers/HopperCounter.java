package carpet.helpers;

import carpet.utils.Messenger;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.util.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.BaseText;
import net.minecraft.text.TranslatableText;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.DimensionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class HopperCounter
{
    public static final Map<DyeColor, HopperCounter> COUNTERS;

    static
    {
        EnumMap<DyeColor, HopperCounter> counterMap = new EnumMap<>(DyeColor.class);
        for (DyeColor color : DyeColor.values())
        {
            counterMap.put(color, new HopperCounter(color));
        }
        COUNTERS = Maps.immutableEnumMap(counterMap);
    }

    public final DyeColor color;
    private final Object2LongMap<Item> counter = new Object2LongLinkedOpenHashMap<>();
    private long startTick;
    private long startMillis;
    // private PubSubInfoProvider<Long> pubSubProvider;

    private HopperCounter(DyeColor color)
    {
        this.color = color;
        // pubSubProvider = new PubSubInfoProvider<>(QuickCarpet.PUBSUB, "carpet.counter." + color.getName(), 0, this::getTotalItems);
    }

    public void add(MinecraftServer server, ItemStack stack)
    {
        if (startTick == 0)
        {
            startTick = server.getWorld(DimensionType.OVERWORLD).getTime();
            startMillis = System.currentTimeMillis();
        }
        Item item = stack.getItem();
        counter.put(item, counter.getLong(item) + stack.getCount());
        // pubSubProvider.publish();
    }

    public void reset(MinecraftServer server)
    {
        counter.clear();
        startTick = server.getWorld(DimensionType.OVERWORLD).getTime();
        startMillis = System.currentTimeMillis();
        // pubSubProvider.publish();
    }

    public static void resetAll(MinecraftServer server)
    {
        for (HopperCounter counter : COUNTERS.values())
        {
            counter.reset(server);
        }
    }

    public static List<BaseText> formatAll(MinecraftServer server, boolean realtime)
    {
        List<BaseText> text = new ArrayList<>();

        for (HopperCounter counter : COUNTERS.values())
        {
            List<BaseText> temp = counter.format(server, realtime, false);
            if (temp.size() > 1)
            {
                if (!text.isEmpty()) text.add(Messenger.s(""));
                text.addAll(temp);
            }
        }
        if (text.isEmpty())
        {
            text.add(Messenger.s("尚未计算任何物品."));
        }
        return text;
    }

    public List<BaseText> format(MinecraftServer server, boolean realTime, boolean brief)
    {
        if (counter.isEmpty())
        {
            if (brief)
            {
                return Collections.singletonList(Messenger.c("g "+color+": -, -/h, - min "));
            }
            return Collections.singletonList(Messenger.s(String.format("还没有物品在 %s", color.getName())));
        }
        long total = getTotalItems();
        long ticks = Math.max(realTime ? (System.currentTimeMillis() - startMillis) / 50 : server.getWorld(DimensionType.OVERWORLD).getTime() - startTick, 1);
        if (total == 0)
        {
            if (brief)
            {
                return Collections.singletonList(Messenger.c(String.format("c %s: 0, 0/h, %.1f min ", color, ticks / (20.0 * 60.0))));
            }
            return Collections.singletonList(Messenger.c(String.format("w 还没有物品在 %s (%.2f min.%s)",
                    color.getName(), ticks / (20.0 * 60.0), (realTime ? " - 现实时间" : "")),
                    "nb  [X]", "^g 重置", "!/counter " + color.getName() +" reset"));
        }
        if (brief)
        {
            return Collections.singletonList(Messenger.c(String.format("c %s: %d, %d/h, %.1f min ",
                    color.getName(), total, total * (20 * 60 * 60) / ticks, ticks / (20.0 * 60.0))));
        }
        List<BaseText> items = new ArrayList<>();
        items.add(Messenger.c(String.format("w Items for %s (%.2f min.%s), 总计: %d, (%.1f/h):",
                color, ticks*1.0/(20*60), (realTime?" - real time":""), total, total*1.0*(20*60*60)/ticks),
                "nb [X]", "^g 重置", "!/counter "+color+" reset"
        ));
        items.addAll(counter.object2LongEntrySet().stream().map(e ->
        {
            BaseText itemName = new TranslatableText(e.getKey().getTranslationKey());
            long count = e.getLongValue();
            return Messenger.c("w - ", itemName, String.format("w : %d, %.1f/h",
                    count,
                    count * (20.0 * 60.0 * 60.0) / ticks));
        }).collect(Collectors.toList()));
        return items;
    }

    public static HopperCounter getCounter(String color)
    {
        try
        {
            DyeColor colorEnum = DyeColor.valueOf(color.toUpperCase(Locale.ROOT));
            return COUNTERS.get(colorEnum);
        }
        catch (IllegalArgumentException e)
        {
            return null;
        }
    }

    public long getTotalItems()
    {
        return counter.values().stream().mapToLong(Long::longValue).sum();
    }
}
