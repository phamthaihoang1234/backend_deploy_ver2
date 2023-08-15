package vn.fs.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.fs.entity.Cart;
import vn.fs.entity.CartDetail;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    private Cart cart;
    private List<CartDetail> orderDetails;
}
