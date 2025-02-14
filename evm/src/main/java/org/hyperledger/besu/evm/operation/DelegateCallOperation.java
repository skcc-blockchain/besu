/*
 * Copyright ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.hyperledger.besu.evm.operation;

import org.hyperledger.besu.datatypes.Address;
import org.hyperledger.besu.datatypes.Wei;
import org.hyperledger.besu.evm.Gas;
import org.hyperledger.besu.evm.account.Account;
import org.hyperledger.besu.evm.frame.MessageFrame;
import org.hyperledger.besu.evm.gascalculator.GasCalculator;
import org.hyperledger.besu.evm.internal.Words;

import org.apache.tuweni.units.bigints.UInt256;

public class DelegateCallOperation extends AbstractCallOperation {

  public DelegateCallOperation(final GasCalculator gasCalculator) {
    super(0xF4, "DELEGATECALL", 6, 1, false, 1, gasCalculator);
  }

  @Override
  protected Gas gas(final MessageFrame frame) {
    return Gas.of(frame.getStackItem(0));
  }

  @Override
  protected Address to(final MessageFrame frame) {
    return Words.toAddress(frame.getStackItem(1));
  }

  @Override
  protected Wei value(final MessageFrame frame) {
    return Wei.ZERO;
  }

  @Override
  protected Wei apparentValue(final MessageFrame frame) {
    return frame.getApparentValue();
  }

  @Override
  protected UInt256 inputDataOffset(final MessageFrame frame) {
    return frame.getStackItem(2);
  }

  @Override
  protected UInt256 inputDataLength(final MessageFrame frame) {
    return frame.getStackItem(3);
  }

  @Override
  protected UInt256 outputDataOffset(final MessageFrame frame) {
    return frame.getStackItem(4);
  }

  @Override
  protected UInt256 outputDataLength(final MessageFrame frame) {
    return frame.getStackItem(5);
  }

  @Override
  protected Address address(final MessageFrame frame) {
    return frame.getRecipientAddress();
  }

  @Override
  protected Address sender(final MessageFrame frame) {
    return frame.getSenderAddress();
  }

  @Override
  public Gas gasAvailableForChildCall(final MessageFrame frame) {
    return gasCalculator().gasAvailableForChildCall(frame, gas(frame), false);
  }

  @Override
  protected boolean isStatic(final MessageFrame frame) {
    return frame.isStatic();
  }

  @Override
  public Gas cost(final MessageFrame frame) {
    final Gas stipend = gas(frame);
    final UInt256 inputDataOffset = inputDataOffset(frame);
    final UInt256 inputDataLength = inputDataLength(frame);
    final UInt256 outputDataOffset = outputDataOffset(frame);
    final UInt256 outputDataLength = outputDataLength(frame);
    final Account recipient = frame.getWorldUpdater().get(address(frame));

    return gasCalculator()
        .callOperationGasCost(
            frame,
            stipend,
            inputDataOffset,
            inputDataLength,
            outputDataOffset,
            outputDataLength,
            Wei.ZERO,
            recipient,
            to(frame));
  }
}
