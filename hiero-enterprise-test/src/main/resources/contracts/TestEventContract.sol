// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.8.0;

contract TestEventContract {
    
    event Transfer(address indexed from, address indexed to, uint256 value);
    event ActionLogged(string action, uint256 timestamp);

    function triggerTransfer(address to, uint256 value) public {
        emit Transfer(msg.sender, to, value);
    }

    function triggerAction(string memory action) public {
        emit ActionLogged(action, block.timestamp);
    }

    function triggerBoth(address to, uint256 value, string memory action) public {
        emit Transfer(msg.sender, to, value);
        emit ActionLogged(action, block.timestamp);
    }
}
