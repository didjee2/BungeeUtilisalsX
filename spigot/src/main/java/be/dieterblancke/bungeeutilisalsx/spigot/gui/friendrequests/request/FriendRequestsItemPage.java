package be.dieterblancke.bungeeutilisalsx.spigot.gui.friendrequests.request;

import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendRequest;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendRequestType;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.ItemPage;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.config.GuiConfigItem;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.item.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Iterator;
import java.util.List;

public class FriendRequestsItemPage extends ItemPage
{

    public FriendRequestsItemPage( final Player player,
                                   final int page,
                                   final int max,
                                   final FriendRequestsGuiConfig guiConfig,
                                   final FriendRequestType type,
                                   final List<FriendRequest> friendRequests )
    {
        super( guiConfig.getRows() * 9 );

        for ( GuiConfigItem item : guiConfig.getItems() )
        {
            if ( ( (FriendRequestGuiConfigItem) item ).isRequestItem() )
            {
                continue;
            }
            if ( !this.shouldShow( page, max, item ) )
            {
                continue;
            }
            for ( int slot : item.getSlots() )
            {
                super.setItem( slot, this.getGuiItem( player, item ) );
            }
        }

        for ( GuiConfigItem item : guiConfig.getItems() )
        {
            if ( !( (FriendRequestGuiConfigItem) item ).isRequestItem() )
            {
                continue;
            }

            final Iterator<FriendRequest> friendRequestIterator = friendRequests.iterator();
            for ( int slot : item.getSlots() )
            {
                if ( !friendRequestIterator.hasNext() )
                {
                    break;
                }
                final FriendRequest data = friendRequestIterator.next();

                super.setItem( slot, this.getFriendGuiItem(
                        player,
                        type,
                        (FriendRequestGuiConfigItem) item,
                        data,
                        "{user-name}", type == FriendRequestType.OUTGOING ? data.getFriendName() : data.getUserName(),
                        "{requested-at}", Utils.formatDate( data.getRequestedAt() )
                ) );
            }
        }
    }

    private GuiItem getFriendGuiItem( final Player player,
                                      final FriendRequestType type,
                                      final FriendRequestGuiConfigItem item,
                                      final FriendRequest requestData,
                                      final Object... placeholders )
    {
        final ItemStack itemStack = item.getItem().buildItem( player, placeholders );

        if ( itemStack.getItemMeta() instanceof SkullMeta )
        {
            final SkullMeta itemMeta = ( (SkullMeta) itemStack.getItemMeta() );
            itemMeta.setOwner( type == FriendRequestType.OUTGOING ? requestData.getFriendName() : requestData.getUserName() );
            itemStack.setItemMeta( itemMeta );
        }

        return this.getGuiItem( item.getAction(), item.getRightAction(), itemStack, placeholders );
    }
}
