﻿#region Directives
using VTDev.Libraries.CEXEngine.Crypto.Cipher.Asymmetric.Interfaces;
using System;
#endregion

namespace VTDev.Libraries.CEXEngine.Crypto.Cipher.Asymmetric.McEliece.MPKCCiphers
{
    /// <summary>
    /// McEliece cipher interface
    /// </summary>
    internal interface IMPKCCiphers : IDisposable
    {
        /// <summary>
        /// Get: The maximum number of bytes the cipher can encrypt
        /// </summary>
        int MaxPlainText { get; }

        /// <summary>
        /// Initialize the cipher
        /// </summary>
        /// 
        /// <param name="Encryption">When true cipher is for encryption, if false, decryption</param>
        /// <param name="KeyPair">The public and private key pair</param>
        void Initialize(bool Encryption, IAsymmetricKeyPair KeyPair);

        /// <summary>
        /// Decrypt a cipher text
        /// </summary>
        /// 
        /// <param name="Input">The cipher text</param>
        /// 
        /// <returns>The plain text</returns>
        byte[] Decrypt(byte[] Input);

        /// <summary>
        /// Encrypt a plain text message
        /// </summary>
        /// 
        /// <param name="Input">The plain text</param>
        /// 
        /// <returns>The cipher text</returns>
        byte[] Encrypt(byte[] Input);
    }
}
