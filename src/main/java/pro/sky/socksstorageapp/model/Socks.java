package pro.sky.socksstorageapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Socks {
    @NotEmpty
    private SocksSize socksSize;
    @NotEmpty
    private SocksColor socksColor;
    @NotEmpty
    private int socksStructure;
    @Positive
    private int quantity;
}
