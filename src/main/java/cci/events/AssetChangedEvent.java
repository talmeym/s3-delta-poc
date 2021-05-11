package cci.events;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AssetChangedEvent implements Event {
    String assetId;
    String propertyName;
    String oldValue;
    String newValue;
}
