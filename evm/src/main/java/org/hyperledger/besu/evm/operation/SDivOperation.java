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

import org.hyperledger.besu.evm.EVM;
import org.hyperledger.besu.evm.frame.MessageFrame;
import org.hyperledger.besu.evm.gascalculator.GasCalculator;

import java.math.BigInteger;
import java.util.Arrays;

import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.units.bigints.UInt256;

public class SDivOperation extends AbstractFixedCostOperation {

  public SDivOperation(final GasCalculator gasCalculator) {
    super(0x05, "SDIV", 2, 1, false, 1, gasCalculator, gasCalculator.getLowTierGasCost());
  }

  @Override
  public OperationResult executeFixedCostOperation(final MessageFrame frame, final EVM evm) {
    final UInt256 value0 = frame.popStackItem();
    final UInt256 value1 = frame.popStackItem();
    if (value1.isZero()) {
      frame.pushStackItem(UInt256.ZERO);
    } else {
      final BigInteger result = value0.toSignedBigInteger().divide(value1.toSignedBigInteger());
      Bytes resultBytes = Bytes.wrap(result.toByteArray());
      if (resultBytes.size() > 32) {
        resultBytes = resultBytes.slice(resultBytes.size() - 32, 32);
      }

      final byte[] padding = new byte[32 - resultBytes.size()];
      Arrays.fill(padding, result.signum() < 0 ? (byte) 0xFF : 0x00);

      frame.pushStackItem(UInt256.fromBytes(Bytes.concatenate(Bytes.wrap(padding), resultBytes)));
    }

    return successResponse;
  }
}
