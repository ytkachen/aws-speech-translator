package com.cisco

import java.io.{ByteArrayOutputStream, InputStream}

/**
  * @author Yuri Tkachenko
  */
package object speech {

  def streamToBytes(input: InputStream): Array[Byte] = {
    val len = 16384
    val buf = Array.ofDim[Byte](len)
    val out = new ByteArrayOutputStream

    def copy(): Array[Byte] = {
      val n = input.read(buf, 0, len)
      if ( n != -1) {
        out.write(buf, 0, len)
        copy()
      } else {
        out.toByteArray
      }
    }

    copy()
  }
}
