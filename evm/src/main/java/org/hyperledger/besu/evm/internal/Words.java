/*
 * Copyright contributors to Hyperledger Besu
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
package org.hyperledger.besu.evm.internal;

import org.hyperledger.besu.datatypes.Address;

import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.bytes.Bytes32;
import org.apache.tuweni.bytes.MutableBytes;
import org.apache.tuweni.units.bigints.UInt256;

/** Static utility methods to work with VM words (that is, {@link Bytes32} values). */
public abstract class Words {
  private Words() {}

  /**
   * Creates a new word containing the provided address.
   *
   * @param address The address to convert to a word.
   * @return A VM word containing {@code address} (left-padded as according to the VM specification
   *     (Appendix H. of the Yellow paper)).
   */
  public static UInt256 fromAddress(final Address address) {
    return UInt256.fromBytes(Bytes32.leftPad(address));
  }

  /**
   * Extract an address from the provided address.
   *
   * @param bytes The word to extract the address from.
   * @return An address build from the right-most 160-bits of the {@code bytes} (as according to the
   *     VM specification (Appendix H. of the Yellow paper)).
   */
  public static Address toAddress(final Bytes32 bytes) {
    return Address.wrap(bytes.slice(bytes.size() - Address.SIZE, Address.SIZE));
  }

  /**
   * Extract an address from the provided address.
   *
   * @param bytes The word to extract the address from.
   * @return An address build from the right-most 160-bits of the {@code bytes} (as according to the
   *     VM specification (Appendix H. of the Yellow paper)).
   */
  public static Address toAddress(final Bytes bytes) {
    int size = bytes.size();
    if (size < 20) {
      MutableBytes result = MutableBytes.create(20);
      bytes.copyTo(result, 20 - size);
      // Addresses get hashed alot in calls, and mutable bytes don't cache the `hashCode`
      // so always return an immutable copy
      return Address.wrap(result.copy());
    } else if (size == 20) {
      return Address.wrap(bytes);
    } else {
      return Address.wrap(bytes.slice(size - Address.SIZE, Address.SIZE));
    }
  }

  /**
   * The number of words corresponding to the provided input.
   *
   * <p>In other words, this computes {@code input.size() / 32} but rounded up.
   *
   * @param input the input to check.
   * @return the number of (32 bytes) words that {@code input} spans.
   */
  public static int numWords(final Bytes input) {
    // m/n round up == (m + n - 1)/n: http://www.cs.nott.ac.uk/~psarb2/G51MPC/slides/NumberLogic.pdf
    return (input.size() + Bytes32.SIZE - 1) / Bytes32.SIZE;
  }
}
