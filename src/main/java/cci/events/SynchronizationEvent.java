package cci.events;

import cci.Asset;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SynchronizationEvent implements Event {
    private Asset asset;
}
