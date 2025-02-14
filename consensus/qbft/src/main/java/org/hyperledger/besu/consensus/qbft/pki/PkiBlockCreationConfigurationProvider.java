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

package org.hyperledger.besu.consensus.qbft.pki;

import static com.google.common.base.Preconditions.checkNotNull;

import org.hyperledger.besu.pki.config.PkiKeyStoreConfiguration;
import org.hyperledger.besu.pki.keystore.KeyStoreWrapper;
import org.hyperledger.besu.pki.keystore.SoftwareKeyStoreWrapper;

import java.nio.file.Path;

import com.google.common.annotations.VisibleForTesting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PkiBlockCreationConfigurationProvider {

  private static final Logger LOG = LogManager.getLogger();

  private final KeyStoreWrapperProvider keyStoreWrapperProvider;

  public PkiBlockCreationConfigurationProvider() {
    this(SoftwareKeyStoreWrapper::new);
  }

  @VisibleForTesting
  PkiBlockCreationConfigurationProvider(final KeyStoreWrapperProvider keyStoreWrapperProvider) {
    this.keyStoreWrapperProvider = checkNotNull(keyStoreWrapperProvider);
  }

  public PkiBlockCreationConfiguration load(
      final PkiKeyStoreConfiguration pkiKeyStoreConfiguration) {
    KeyStoreWrapper keyStore;
    try {
      keyStore =
          keyStoreWrapperProvider.apply(
              pkiKeyStoreConfiguration.getKeyStoreType(),
              pkiKeyStoreConfiguration.getKeyStorePath(),
              pkiKeyStoreConfiguration.getKeyStorePassword(),
              null);
      LOG.info("Loaded PKI Block Creation KeyStore {}", pkiKeyStoreConfiguration.getKeyStorePath());
    } catch (Exception e) {
      final String message = "Error loading PKI Block Creation KeyStore";
      LOG.error(message, e);
      throw new RuntimeException(message, e);
    }

    KeyStoreWrapper trustStore;
    try {
      trustStore =
          keyStoreWrapperProvider.apply(
              pkiKeyStoreConfiguration.getTrustStoreType(),
              pkiKeyStoreConfiguration.getTrustStorePath(),
              pkiKeyStoreConfiguration.getTrustStorePassword(),
              pkiKeyStoreConfiguration.getCrlFilePath().orElse(null));
      LOG.info(
          "Loaded PKI Block Creation TrustStore {}", pkiKeyStoreConfiguration.getTrustStorePath());
    } catch (Exception e) {
      final String message = "Error loading PKI Block Creation TrustStore";
      LOG.error(message, e);
      throw new RuntimeException(message, e);
    }

    return new PkiBlockCreationConfiguration(
        keyStore, trustStore, pkiKeyStoreConfiguration.getCertificateAlias());
  }

  @FunctionalInterface
  interface KeyStoreWrapperProvider {

    KeyStoreWrapper apply(
        final String keyStoreType,
        final Path keyStorePath,
        final String keyStorePassword,
        final Path crl);
  }
}
