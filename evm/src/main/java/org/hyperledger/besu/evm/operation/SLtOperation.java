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

import org.apache.tuweni.units.bigints.UInt256;

public class SLtOperation extends AbstractFixedCostOperation {

  public SLtOperation(final GasCalculator gasCalculator) {
    super(0x12, "SLT", 2, 1, false, 1, gasCalculator, gasCalculator.getVeryLowTierGasCost());
  }

  @Override
  public OperationResult executeFixedCostOperation(final MessageFrame frame, final EVM evm) {
    final UInt256 value0 = frame.popStackItem();
    final UInt256 value1 = frame.popStackItem();

    final BigInteger b0 = value0.toSignedBigInteger();
    final BigInteger b1 = value1.toSignedBigInteger();

    final UInt256 result = b0.compareTo(b1) < 0 ? UInt256.ONE : UInt256.ZERO;

    frame.pushStackItem(result);

    return successResponse;
  }
}
