package cci;

import cci.events.*;

import java.util.*;
import java.util.stream.Collectors;

public class DeltaEngine {

    public List<Event> determineDelta(Asset[] currentData, Asset[] prevData) {
        if(prevData == null) {
            return Arrays.stream(currentData).map(SynchronizationEvent::new).collect(Collectors.toList());
        }

        List<Event> events = new ArrayList<>();
        Map<String, Asset> newDataById = Arrays.stream(currentData).collect(Collectors.toMap(Asset::getId, asset -> asset));
        Map<String, Asset> prevDataById = Arrays.stream(prevData).collect(Collectors.toMap(Asset::getId, asset -> asset));
        newDataById.keySet().stream().filter(id -> !prevDataById.containsKey(id)).forEach(id -> events.add(new AssetAddedEvent(newDataById.get(id))));
        prevDataById.keySet().stream().filter(id -> !newDataById.containsKey(id)).forEach(id -> events.add(new AssetRemovedEvent(id)));
        newDataById.values().stream().filter(a -> prevDataById.containsKey(a.getId())).forEach(a -> events.addAll(determineDelta(a, prevDataById.get(a.getId()))));

        return events;
    }

    private List<Event> determineDelta(Asset newAsset, Asset prevAsset) {
        if(!newAsset.getAddress().equals(prevAsset.getAddress())) {
            return Collections.singletonList(new AssetChangedEvent(newAsset.id, "address", prevAsset.address, newAsset.address));
        }

        return new ArrayList<>();
    }
}
