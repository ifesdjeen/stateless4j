package com.googlecode.stateless4j.conversion;

import com.googlecode.stateless4j.exceptions.ArgOfTypeRequiredInPosition;
import com.googlecode.stateless4j.exceptions.TooManyParameters;
import com.googlecode.stateless4j.exceptions.WrongArgType;

import javax.annotation.Nonnull;

public class ParameterConversion {
  public static Object Unpack(@Nonnull Object[] args,
                              Class<?> argType,
                              int index) throws ArgOfTypeRequiredInPosition, WrongArgType {
    if (args.length <= index)
      throw new ArgOfTypeRequiredInPosition(argType, index);

    Object arg = args[index];

    if (arg != null && !argType.isAssignableFrom(arg.getClass()))
      throw new WrongArgType(arg.getClass(), index);

    return arg;
  }

  public static void Validate(Object[] args, Class<?>[] expected)
          throws TooManyParameters, ArgOfTypeRequiredInPosition, WrongArgType {
    if (args.length > expected.length)
      throw new TooManyParameters(expected.length, args.length);

    for (int i = 0; i < expected.length; ++i)
      Unpack(args, expected[i], i);
  }
}
